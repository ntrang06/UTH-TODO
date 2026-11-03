package com.example.uthtodo.data

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.uthtodo.R
import com.example.uthtodo.model.AppUser
import com.example.uthtodo.model.SignInResult
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(context: Context) {

    // Đóng gói các đối tượng xử lý bên trong repository.
    private val appContext = context.applicationContext

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val credentialManager = CredentialManager.create(
        appContext
    )

    // Lấy người dùng đang đăng nhập.
    fun getCurrentUser(): AppUser? {
        return firebaseAuth.currentUser?.let { user ->
            mapUser(user)
        }
    }

    // Xử lý khi nhấn nút Tiếp tục với Google.
    suspend fun signInWithGoogle(
        activity: Activity
    ): SignInResult {
        return try {
            val googleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = appContext.getString(
                    R.string.default_web_client_id
                )
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()

            // Mở giao diện chọn tài khoản Google.
            val response = credentialManager.getCredential(
                context = activity,
                request = request
            )

            val credential = response.credential

            if (
                credential !is CustomCredential ||
                credential.type !=
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return SignInResult.Error(
                    "Không nhận được thông tin đăng nhập Google hợp lệ."
                )
            }

            // Đọc Google ID Token.
            val googleCredential = GoogleIdTokenCredential.createFrom(
                credential.data
            )

            // Đăng nhập Firebase bằng token vừa nhận.
            val user = authenticateWithFirebase(
                googleCredential.idToken
            )

            SignInResult.Success(user)

        } catch (_: GetCredentialCancellationException) {
            // Người dùng chủ động đóng cửa sổ chọn tài khoản.
            SignInResult.Cancelled

        } catch (_: NoCredentialException) {
            SignInResult.Error(
                "Không tìm thấy tài khoản Google phù hợp. " +
                        "Hãy kiểm tra tài khoản Google trên thiết bị."
            )

        } catch (_: FirebaseNetworkException) {
            SignInResult.Error(
                "Không kết nối được máy chủ. Hãy kiểm tra Internet."
            )

        } catch (error: FirebaseAuthException) {
            SignInResult.Error(
                "Đăng nhập Firebase thất bại: ${error.errorCode}"
            )

        } catch (_: GetCredentialException) {
            SignInResult.Error(
                "Không thể đăng nhập Google. " +
                        "Hãy kiểm tra cấu hình Firebase và thử lại."
            )

        } catch (error: CancellationException) {
            // Giữ đúng cơ chế hủy của coroutine.
            throw error

        } catch (error: Exception) {
            SignInResult.Error(
                error.localizedMessage
                    ?: "Đăng nhập thất bại. Vui lòng thử lại."
            )
        }
    }

    // Đăng nhập Firebase và đợi kết quả.
    private suspend fun authenticateWithFirebase(
        idToken: String
    ): AppUser {
        return suspendCancellableCoroutine { continuation ->
            val firebaseCredential = GoogleAuthProvider.getCredential(
                idToken,
                null
            )

            firebaseAuth.signInWithCredential(firebaseCredential)
                .addOnSuccessListener { result ->
                    if (continuation.isActive) {
                        val user = result.user

                        if (user != null) {
                            continuation.resume(
                                mapUser(user)
                            )
                        } else {
                            continuation.resumeWithException(
                                IllegalStateException(
                                    "Không lấy được thông tin người dùng."
                                )
                            )
                        }
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }
    }

    // Đăng xuất Firebase và xóa trạng thái chọn tài khoản.
    suspend fun signOut() {
        firebaseAuth.signOut()

        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
    }

    // Chuyển dữ liệu Firebase thành đối tượng của ứng dụng.
    private fun mapUser(user: FirebaseUser): AppUser {
        return AppUser(
            uid = user.uid,
            displayName = user.displayName
                ?.takeIf { it.isNotBlank() }
                ?: "Người dùng UTH",
            email = user.email.orEmpty(),
            photoUrl = user.photoUrl?.toString()
        )
    }
}