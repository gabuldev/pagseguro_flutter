import 'package:flutter/material.dart';
import 'package:pagseguro_flutter/pagseguro_flutter.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PagSeguro Example',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'PagSeguro - by Gabul Dev'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var pag;

  @override
  void initState() {
    pag = PlugPag(onState: (state) {
      if (Navigator.canPop(context)) {
        Navigator.pop(context);
      }
      showDialog(
          context: context,
          builder: (context) => AlertDialog(
                content: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    if (state.type == Type.loading) CircularProgressIndicator(),
                    Text(state.message)
                  ],
                ),
              ));
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text(
                "Antes de tudo, faça o pareamento da sua máquina de cartão!",
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.red, fontSize: 20),
              ),
              Text(
                "Se nao fizer isso o aplicativo vai fechar, estou resolvendo isso...",
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.red, fontSize: 20),
              ),
              SizedBox(
                height: 30,
              ),
              Text("Execute os botões na ordem"),
              FlatButton(
                child: Text("Pedir permissões"),
                onPressed: () {
                  pag.requestPermissions();
                },
              ),
              FlatButton(
                child: Text("Autenticar no PagSeguro"),
                onPressed: () {
                  pag.requestAuthentication();
                },
              ),
              FlatButton(
                child: Text("Verificar o status da autenticacao"),
                onPressed: () {
                  pag.checkAuthentication();
                },
              ),
              Text("Se for uma minizinha ou similares"),
              FlatButton(
                child: Text(
                    "Realizar uma trasacao de debito no valor de R\$: 2,00"),
                onPressed: () {
                  pag.startPinpadDebitPayment(2.00);
                },
              ),
              Text("Se for uma a pro use esse"),
              FlatButton(
                child: Text(
                    "Realizar uma trasacao de debito no valor de R\$: 2,00"),
                onPressed: () {
                  pag.startTerminalDebitPayment(2.00);
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
