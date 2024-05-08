
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.wrap(newLocale: Locale): Context {
  var context = this
  val res = context.resources
  val configuration = Configuration(res.configuration)

  configuration.setLocale(newLocale)

  val localeList = android.os.LocaleList(newLocale)
  android.os.LocaleList.setDefault(localeList)
  configuration.setLocales(localeList)

  context = context.createConfigurationContext(configuration)

  return context
}
