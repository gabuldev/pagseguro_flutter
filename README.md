# pagseguro_flutter

## Sobre

O pacote tem como objetivo facilitar o uso das tecnologias do pagseguro.

- PlugPag
  Agora você pode integrar os seus aplicativos Flutter com as máquinas de cartão através da tecnologia BLUETOOTH.

  Para mais informações acesse:
   [Documento Oficial](https://dev.pagseguro.uol.com.br/)


# Setup - Android



#### Adicione no seu AndroidManifest.xml

Antes
``` 
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="plugpag.dev.gabul.pagseguro_flutter_example">
```

Depois

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="YOUR_PACKAGE_NAME"
    xmlns:tools="http://schemas.android.com/tools">
```
No mesmo arquivo precisa adicionar mais uma linha
```
android:name="io.flutter.app.FlutterApplication"
android:label="pagseguro_flutter_example"

//ADICIONE TOOLS EM BAIXO DO LABEL
tools:replace="android:label"
```

### Agora vamos adicionar no app/build.gradle

```
 dependencies {
    ...
    
    implementation 'com.android.support:design:28.0.0'
    implementation 'br.com.uol.pagseguro:plugpag:3.1.2'
    implementation 'com.android.support:support-annotations:28.0.0'
}


```
## Sistemas

    ANDROID - OK
    IOS - IMPLEMENTAR
