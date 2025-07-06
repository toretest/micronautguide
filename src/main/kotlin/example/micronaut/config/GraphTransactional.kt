package example.micronaut.config

import io.micronaut.aop.Around
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import jakarta.inject.Singleton
//import no.di.optimize.data.shared.configuration.gremlin.service.GremlinInterface




@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@Around()
annotation class GraphTransactional()


/*
@Singleton
@InterceptorBean(GraphTransactional::class)
class GraphTransactionInterceptor: MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        if (context.target is GremlinInterface) {
            val target: GremlinInterface = context.target as GremlinInterface
            val client = target.client
            val g = target.g
            if (client.transactional()) {
                try {
                    g.tx().open()
                    val result = context.proceed()
                    g.tx().commit()
                    return result
                } catch (ex: Exception) {
                    g.tx().rollback()
                    throw ex
                }
            }
        }
        return context.proceed()
    }
}*/
