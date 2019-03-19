package com.doit.test.data.layer.auth

import android.content.SharedPreferences
import com.doit.test.TestApp
import com.doit.test.data.layer.auth.abstractions.AuthRepository
import com.doit.test.data.layer.auth.model.AuthRequest
import com.doit.test.data.layer.auth.model.AuthResponse
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.Exception
import javax.inject.Inject

@Suppress("ProtectedInFinal")
class DoItAuthRepository : AuthRepository {

    private interface Api {
        @POST("users")
        fun signUp(@Body body: AuthRequest): Single<AuthResponse>

        @POST("auth")
        fun signIn(@Body body: AuthRequest): Single<AuthResponse>
    }

    private val tokenSubject: BehaviorSubject<String>
    private val authKey = "Authorization"
    private val emailKey = "email"
    private val passKey = "pass"
    private val tokenKey = "token"
    private val api: Api

    @Inject
    protected lateinit var prefs: SharedPreferences
    @Inject
    protected lateinit var clientBuilder: OkHttpClient.Builder
    @Inject
    protected lateinit var retrofitBuilder: Retrofit.Builder

    init {
        TestApp.coreComponent.inject(this)

        this.tokenSubject = BehaviorSubject.createDefault(this.prefs.getString(this.tokenKey, "")!!)

        this.api = this.retrofitBuilder.build().create(Api::class.java)
        this.clientBuilder
            .addInterceptor(
                object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        return chain.proceed(
                            chain.request().newBuilder()
                                .addHeader(authKey, tokenSubject.blockingFirst())
                                .build()
                        )
                    }
                }
            )
            .authenticator(
                object : Authenticator {
                    override fun authenticate(route: Route, response: Response): Request? {

                        var token: String? = null
                        try {
                            token = api.signIn(
                                AuthRequest(
                                    prefs.getString(emailKey, "")!!,
                                    prefs.getString(passKey, "")!!
                                )
                            )
                                .blockingGet()
                                .token
                        } catch (e: Exception) {
                            tokenSubject.onNext(token?: "")
                        }

                        return response.request().newBuilder()
                            .addHeader(authKey, tokenSubject.blockingFirst())
                            .build()


                    }
                }
            )
    }

    override fun observeAuth() = this.tokenSubject.map { !it.isEmpty() }!!
    override fun getEmail() = Single.just(this.prefs.getString(this.emailKey, ""))

    private fun authenticate(
        email: String,
        password: String,
        func: (AuthRequest) -> Single<AuthResponse>
    ): Completable {
        return func.invoke(AuthRequest(email, password))
            .onErrorResumeNext { t ->
                Single.error<AuthResponse> {

                    if (t is HttpException && t.code() == 422) {

                        val mediaType = MediaType.parse("application/json")

                        val response = t.response()
                        val body = response.errorBody()!!.string()

                        when {
                            body.contains("Has no user with such email") -> throw HttpException(
                                retrofit2.Response.error<AuthResponse>(
                                    403,
                                    ResponseBody.create(mediaType, "forbidden")
                                )
                            )
                            body.contains("The email has already been taken") -> throw HttpException(
                                retrofit2.Response.error<AuthResponse>(
                                    409,
                                    ResponseBody.create(mediaType, "forbidden")
                                )
                            )
                        }

                    }

                    throw t

                }
            }
            .flatMapCompletable { response ->
                CompletableSource {
                    val token = "Bearer " + response.token
                    this.prefs.edit().putString(this.emailKey, email).apply()
                    this.prefs.edit().putString(this.passKey, password).apply()
                    this.prefs.edit().putString(this.tokenKey, token).apply()

                    this.tokenSubject.onNext(token)
                    it.onComplete()
                }
            }
    }

    override fun signUp(email: String, password: String) = this.authenticate(email, password) { this.api.signUp(it) }
    override fun signIn(email: String, password: String) = this.authenticate(email, password) { this.api.signIn(it) }

    override fun signOut() = Completable.fromAction {
        this.prefs.edit().remove(tokenKey).apply()
        this.tokenSubject.onNext("")
    }

}