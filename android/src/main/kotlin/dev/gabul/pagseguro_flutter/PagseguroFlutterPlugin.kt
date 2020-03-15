package dev.gabul.pagseguro_flutter

import android.text.TextUtils
import io.flutter.plugin.common.MethodChannel.*
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.common.*
import  br.com.uol.pagseguro.plugpag.PlugPagAuthenticationListener
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult
import br.com.uol.pagseguro.plugpag.PlugPagVoidData
import br.com.uol.pagseguro.plugpag.PlugPagPaymentData
import br.com.uol.pagseguro.plugpag.PlugPag
import dev.gabul.pagseguro_flutter.task.PinpadVoidPaymentTask
import dev.gabul.pagseguro_flutter.task.PinpadPaymentTask
import dev.gabul.pagseguro_flutter.task.TerminalQueryTransactionTask
import dev.gabul.pagseguro_flutter.task.TerminalVoidPaymentTask
import dev.gabul.pagseguro_flutter.task.TerminalPaymentTask
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.pm.PackageInfo
import androidx.core.app.ActivityCompat
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import android.app.Activity



class PagseguroFlutterPlugin():  FlutterActivity(), MethodCallHandler, TaskHandler, PlugPagAuthenticationListener {

  private val PERMISSIONS_REQUEST_CODE = 0x1234
  private val CHANNEL = "pagseguro_flutter"
  private var context: Activity? = null
  private var methodChannel: MethodChannel? = null

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(),  "pagseguro_flutter")
      channel.setMethodCallHandler(PagseguroFlutterPlugin(registrar.activity(),channel))
    }
  }

  constructor( activity: Activity, methodChannel: MethodChannel) : this() {
    this.context = activity
    this.methodChannel = methodChannel
    PlugPagManager.create(activity)
  }


  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      //PERMISSIONS
      "requestPermission" ->  this.requestPermissions()

      //Auth
      "requestAuthentication" -> this.requestAuthentication()
      "checkAuthentication" -> this.checkAuthentication()
      "invalidateAuthentication" -> this.invalidateAuthentication()

      //Terminal
      "startTerminalCreditPayment" -> this.startTerminalCreditPayment(call.arguments as Int)
      "startTerminalCreditWithInstallmentsPayment" -> this.startTerminalCreditWithInstallmentsPayment((call.arguments as List<*>)[0] as Int,(call.arguments as List<*>)[1] as Int)
      "startTerminalDebitPayment" -> this.startTerminalDebitPayment(call.arguments as Int)
      "startTerminalVoucherPayment" -> this.startTerminalVoucherPayment(call.arguments as Int)
      "startTerminalVoidPayment" -> this.startTerminalVoidPayment()
      "startTerminalQueryTransaction" -> this.startTerminalQueryTransaction()

      //Pinpad
      "startPinpadCreditPayment" -> this.startPinpadCreditPayment(call.arguments as Int)
      "startPinpadCreditWithInstallmentsPayment" -> this.startPinpadCreditWithInstallmentsPayment((call.arguments as List<*>)[0] as Int,(call.arguments as List<*>)[1] as Int)
      "startPinpadDebitPayment" -> this.startPinpadDebitPayment(call.arguments as Int)
      "startPinpadVoucherPayment" -> this.startPinpadVoucherPayment(call.arguments as Int)
      "startPinpadVoidPayment" -> this.startPinpadVoidPayment()

      //Transation
      "cancelCurrentTransation" -> this.cancelCurrentTransation()

      else -> result.notImplemented()
    }
  }


  // -----------------------------------------------------------------------------------------------------------------
  // Request missing permissions
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Requests permissions on runtime, if any needed permission is not granted.
   */
  private fun requestPermissions() {
    var missingPermissions: Array<String>? = null

    missingPermissions = this.filterMissingPermissions(this.getManifestPermissions())

    if (missingPermissions != null && missingPermissions.size > 0) {
      ActivityCompat.requestPermissions(this.context!!, missingPermissions, PERMISSIONS_REQUEST_CODE)
    } else {
      this.showMessage("Todas permissões concedidas")
    }
  }

  /**
   * Returns a list of permissions requested on the AndroidManifest.xml file.
   *
   * @return Permissions requested on the AndroidManifest.xml file.
   */
  private fun getManifestPermissions(): Array<String> {
    var permissions: Array<String>? = null
    var info: PackageInfo? = null
    var pm: PackageManager? = this?.context?.packageManager

    try {
      info = pm?.getPackageInfo(this.context?.packageName, PackageManager.GET_PERMISSIONS)
      permissions = info!!.requestedPermissions
    } catch (e: PackageManager.NameNotFoundException) {

    }

    if (permissions == null) {
      permissions = arrayOf()
    }

    return permissions
  }

  /**
   * Filters only the permissions still not granted.
   *
   * @param permissions List of permissions to be checked.
   * @return Permissions not granted.
   */
  private fun filterMissingPermissions(permissions: Array<String>?): Array<String>? {
    var missingPermissions: Array<String>? = null
    var list: MutableList<String>? = null

    list = ArrayList()

    if (permissions != null && permissions.size > 0) {
      for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this?.context?.applicationContext!!, permission) != PackageManager.PERMISSION_GRANTED) {
          list.add(permission)
        }
      }
    }

    missingPermissions = list.toTypedArray()

    return missingPermissions
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Authentication handling
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Checks if a user is authenticated.
   */
  private fun checkAuthentication(){
    if (PlugPagManager.getInstance().plugPag.isAuthenticated) {
      this.showMessage("Usuario autenticado")
    } else {
      this.showMessage("Usuario nao autenticado")
    }
  }

  /**
   * Requests authentication.
   */
  private fun requestAuthentication() {
    PlugPagManager.getInstance().plugPag.requestAuthentication(this)
  }

  /**
   * Invalidates current authentication.
   */
  private fun invalidateAuthentication() {
    PlugPagManager.getInstance().plugPag.invalidateAuthentication()
  }
  
  
  /**
   * Invalidates current authentication.
   */
  private fun cancelCurrentTransation() {
    PlugPagManager.getInstance().plugPag.abort();
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Terminal transactions
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Starts a new credit payment on a terminal.
   */
  private fun startTerminalCreditPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_CREDITO)
            .setInstallmentType(PlugPag.INSTALLMENT_TYPE_A_VISTA)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()

    TerminalPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new credit payment with installments on a terminal
   */
  private fun startTerminalCreditWithInstallmentsPayment(amount: Int, installments: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_CREDITO)
            .setInstallmentType(PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR)
            .setInstallments(installments)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    TerminalPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new debit payment on a terminal.
   */
  private fun startTerminalDebitPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_DEBITO)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    TerminalPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new voucher payment on a terminal.
   */
  private fun startTerminalVoucherPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_VOUCHER)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    TerminalPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new void payment on a terminal.
   */
  private fun startTerminalVoidPayment() {
    TerminalVoidPaymentTask(this).execute()
  }

  /**
   * Starts a new transaction query on a terminal.
   */
  private fun startTerminalQueryTransaction() {
    TerminalQueryTransactionTask(this).execute()
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Pinpad transactions
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Starts a new credit payment on a pinpad.
   */
  private fun startPinpadCreditPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_CREDITO)
            .setInstallmentType(PlugPag.INSTALLMENT_TYPE_A_VISTA)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    PinpadPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new credit payment with installments on a pinpad.
   */
  private fun startPinpadCreditWithInstallmentsPayment(amount: Int,installments: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_CREDITO)
            .setInstallmentType(PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR)
            .setInstallments(installments)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    PinpadPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new debit payment on a pinpad.
   */
  private fun startPinpadDebitPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_DEBITO)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    PinpadPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a new voucher payment on a pinpad.
   */
  private fun startPinpadVoucherPayment(amount: Int) {
    var paymentData: PlugPagPaymentData? = null

    paymentData = PlugPagPaymentData.Builder()
            .setType(PlugPag.TYPE_VOUCHER)
            .setAmount(amount)
            .setUserReference("CODVENDA")
            .build()
    PinpadPaymentTask(this).execute(paymentData)
  }

  /**
   * Starts a void payment on a pinpad.
   */
  private fun startPinpadVoidPayment() {
    var voidData: PlugPagVoidData? = null
    var lastTransaction: Array<String>? = null

    lastTransaction = PreviousTransactions.pop()

    if (lastTransaction != null) {
      voidData = PlugPagVoidData.Builder()
              .setTransactionCode(lastTransaction[0])
              .setTransactionId(lastTransaction[1])
              .build()
      PinpadVoidPaymentTask(this).execute(voidData)
    } else {
      this.showErrorMessage("Sem dados para estorno")
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // AlertDialog
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Shows an AlertDialog with a simple message.
   *
   * @param message Message to be displayed.
   */
  private fun showMessage(message: String?) {

    if (TextUtils.isEmpty(message)) {
      this.methodChannel?.invokeMethod("showMessage","Erro inesperado")
    } else {
      this.methodChannel?.invokeMethod("showMessage",message)
      //  MethodChannel(flutterView, CHANNEL).invokeMethod("showMessage",message)
    }
  }

  /**
   * Shows an AlertDialog with a simple message.
   *
   * @param message Resource ID of the message to be displayed.
   */
  private fun showMessage( message: Int) {
    var msg: String? = null

    msg = this.getString(message)
    this.showMessage(msg)
  }

  /**
   * Shows an AlertDialog with an error message.
   *
   * @param message Message to be displayed.
   */
  private fun showErrorMessage(message: String) {

    if (TextUtils.isEmpty(message)) {
      this.methodChannel?.invokeMethod("showErrorMessage","Erro inesperado")
    } else {
      this.methodChannel?.invokeMethod("showErrorMessage","Error")
    }

  }

  /**
   * Shows an AlertDialog with an error message.
   *
   * @param message Resource ID of the message to be displayed.
   */
  private fun showErrorMessage( message: Int) {
    var msg: String? = null

    msg = this.getString(message)
    this.showErrorMessage(msg)
  }

  /**
   * Shows an AlertDialog with a ProgressBar.
   *
   * @param message Message to be displayed along-side with the ProgressBar.
   */
  private fun showProgressDialog( message: String?) {
    var msg: String? = null

    if (message == null) {
      msg = "Aguarde"
    } else {
      msg = message
    }
    if (TextUtils.isEmpty(msg)) {
      this.methodChannel?.invokeMethod("showProgressDialog",msg)
    } else {
      this.methodChannel?.invokeMethod("showProgressDialog",msg)
    }

  }

  /**
   * Shows an AlertDialog with a ProgressBar.
   *
   * @param message Resource ID of the message to be displayed along-side with the ProgressBar.
   */
  private fun showProgressDialog(message: Int) {
    var msg: String? = null

    msg = this.getString(message)
    this.showProgressDialog(msg)
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Task handling
  // -----------------------------------------------------------------------------------------------------------------

  override fun onTaskStart() {
    this.showProgressDialog("Aguarde")
  }

  override fun onProgressPublished(progress: String, transactionInfo: Any) {
    var message: String? = null
    var type: String? = null

    if (TextUtils.isEmpty(progress)) {
      message = "Aguarde"
    } else {
      message = progress
    }

    if (transactionInfo is PlugPagPaymentData) {
      when (transactionInfo.type) {
        PlugPag.TYPE_CREDITO -> type = "Crédito"

        PlugPag.TYPE_DEBITO -> type = "Débito"

        PlugPag.TYPE_VOUCHER -> type = "Voucher"
      }

      message =   "Tipo: $type \nValor: R$ ${transactionInfo.amount.toDouble() / 100.0}\nParcelamento: ${transactionInfo.amount.toDouble() / 100.0}\n==========\n ${message}"
    } else if (transactionInfo is PlugPagVoidData) {
      message = "Estorno\\n==========\\n${message}"
    }

    this.showProgressDialog(message)
  }

  override fun onTaskFinished(result: Any) {
    if (result is PlugPagTransactionResult) {
      this.showResult(result)
    } else if (result is String) {
      this.showMessage(result)
    }
  }

  // -----------------------------------------------------------------------------------------------------------------
  // Result display
  // -----------------------------------------------------------------------------------------------------------------

  /**
   * Shows a transaction's result.
   *
   * @param result Result to be displayed.
   */
  private fun showResult(result: PlugPagTransactionResult) {
    var resultDisplay: String? = null
    var lines: MutableList<String>? = null

    if (result == null) {
      throw RuntimeException("Transaction result cannot be null")
    }

    lines = ArrayList()
    lines.add("Resultado: ${result.result}")

    if (!TextUtils.isEmpty(result.errorCode)) {
      lines.add("Codigo de error: ${result.errorCode}")
    }

    if (!TextUtils.isEmpty(result.amount)) {
      var value: String? = null

      value = String.format("%.2f",
              java.lang.Double.parseDouble(result.amount) / 100.0)
      lines.add("Valor: $value")
    }

    if (!TextUtils.isEmpty(result.availableBalance)) {
      var value: String? = null

      value = String.format("%.2f",
              java.lang.Double.parseDouble(result.amount) / 100.0)
      lines.add("Valor disponivel: $value")
    }

    if (!TextUtils.isEmpty(result.bin)) {
      lines.add("BIN: ${result.bin}")
    }

    if (!TextUtils.isEmpty(result.cardBrand)) {
      lines.add("Bandeira: ${result.cardBrand}")
    }

    if (!TextUtils.isEmpty(result.date)) {
      lines.add("Data:  ${result.date}")
    }

    if (!TextUtils.isEmpty(result.time)) {
      lines.add("Hora: ${result.time}")
    }

    if (!TextUtils.isEmpty(result.holder)) {
      lines.add("Titular:  ${result.holder}")
    }

    if (!TextUtils.isEmpty(result.hostNsu)) {
      lines.add("Host NSU:  ${result.hostNsu} ")
    }

    if (!TextUtils.isEmpty(result.message)) {
      lines.add("Menssagem: ${result.message}")
    }

    if (!TextUtils.isEmpty(result.terminalSerialNumber)) {
      lines.add("Numero de serie: ${result.terminalSerialNumber}")
    }

    if (!TextUtils.isEmpty(result.transactionCode)) {
      lines.add("Código da transação:  ${result.transactionCode}")
    }

    if (!TextUtils.isEmpty(result.transactionId)) {
      lines.add( "ID da transação; ${result.transactionId}")
    }

    if (!TextUtils.isEmpty(result.userReference)) {
      lines.add("Código de venda: ${result.userReference}")
    }

    resultDisplay = TextUtils.join("\n", lines)
    this.showMessage(resultDisplay)
  }

  override fun onSuccess() {
    this.showMessage("Usuario autenticado com sucesso")
  }

  override fun onError() {
    this.showMessage("Usuario nao autenticado")
  }
}
