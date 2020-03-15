import 'package:flutter/services.dart';

enum Type { message, error, loading }

///STATES ABOUT MACHINE
class StatesPlugPag {
  final Type type;
  final String message;
  StatesPlugPag(this.type, this.message);
}

class PlugPag {
  MethodChannel platform;
  String device;
  final Function(StatesPlugPag state) onState;

  PlugPag({this.onState}) {
    platform = MethodChannel("pagseguro_flutter");
    platform.setMethodCallHandler((a) => handler(a));
  }

  handler(MethodCall call) {
    if (call.method == "showMessage") {
      onState(StatesPlugPag(Type.message, call.arguments.toString()));
    } else if (call.method == "showErrorMessage") {
      onState(StatesPlugPag(Type.error, call.arguments.toString()));
    } else {
      onState(StatesPlugPag(Type.loading, call.arguments.toString()));
    }
  }

  //!HELPER
  int convertDouble(double amount) => (amount * 100).toInt();

//!PERMISSION
  ///Requests permissions on runtime, if any needed permission is not granted.
  void requestPermissions() async {
    try{
      await platform.invokeMethod("requestPermission");
    }catch(e){
      throw e;
    }
  }

//! AUTH

  ///Requests authentication.
  void requestAuthentication() async {
    try{
      await platform.invokeMethod("requestAuthentication");
    }catch(e){
      throw e;
    }
  }

  ///Requests authentication
  Future<bool> checkAuthentication() async {
    return await platform.invokeMethod("checkAuthentication");
  }

  ///Invalidates current authentication
  void invalidateAuthentication() async {
    await platform.invokeMethod("invalidateAuthentication");
  }

//!TERMINAL
  void startTerminalCreditPayment(double amount) async {
    await platform.invokeMethod(
        "startTerminalCreditPayment", convertDouble(amount));
  }

  void startTerminalCreditWithInstallmentsPayment(
      double amount, int installments) async {
    await platform.invokeMethod('startTerminalCreditWithInstallmentsPayment',
        [convertDouble(amount), installments]);
  }

  void startTerminalDebitPayment(double amount) async {
    await platform.invokeMethod(
        'startTerminalDebitPayment', convertDouble(amount));
  }

  void startTerminalVoucherPayment(double amount) async {
    await platform.invokeMethod(
        'startTerminalVoucherPayment', convertDouble(amount));
  }

  void startTerminalVoidPayment() async {
    await platform.invokeMethod('startTerminalVoidPayment');
  }

  void startTerminalQueryTransaction() async {
    await platform.invokeMethod('startTerminalQueryTransaction');
  }

  //! PINPAD

  void startPinpadCreditPayment(double amount) async {
    await platform.invokeMethod(
        'startPinpadCreditPayment', convertDouble(amount));
  }

  void startPinpadCreditWithInstallmentsPayment(
      double amount, int installments) async {
    await platform.invokeMethod('startPinpadCreditWithInstallmentsPayment',
        [convertDouble(amount), installments]);
  }

  void startPinpadDebitPayment(double amount) async {
    await platform.invokeMethod(
        'startPinpadDebitPayment', convertDouble(amount));
  }

  void startPinpadVoucherPayment(double amount) async {
    await platform.invokeMethod(
        'startPinpadVoucherPayment', convertDouble(amount));
  }

  void startPinpadVoidPayment() async {
    await platform.invokeMethod('startPinpadVoidPayment');
  }

  //! Transation
  Future<void> cancelCurrentTransation() {
    return platform.invokeMethod("cancelCurrentTransation");
  }
}
