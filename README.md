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


## Códigos de retorno
   Os códigos de retorno descritos abaixo são obtidos ao chamar o método getResult() de um PlugPagTransactionResult retornado por um dos métodos de transação de um objeto PlugPag: doPayment(PlugPagPaymentData), voidPayment(PlugPagVoidData) e getLastApprovedTransaction().
   
   |Valor|Descrição|Ação|
   |-----|---------|----|
   |0    |	Transação concluída com sucesso.|	Coletar log (se existir) e enviar para o suporte.|
   |1001 |	Mensagem gerada maior que buffer dimensionado.|	Coletar log (se existir) e enviar para o suporte.|
   |1002 |	Parâmetro de aplicação inválido.|	Coletar log (se existir) e enviar para o suporte.|
   |1003 |	Terminal não está pronto para transacionar.|	Tente novamente.|
   |1004 |	Transação não realizada.|	Verificar mensagem retornada.|
   |1005 |	Buffer de resposta da transação inválido ao obter as informações de resultado da transação.|	Realizar consulta de última transação.|
   |1006 |	Parâmetro de valor da transação não pode ser nulo.|	Verificar implementação da chamada da biblioteca.|
   |1007 |	Parâmetro de valor total da transação não pode ser nulo.|	Verificar implementação da chamada da biblioteca.|
   |1008 |	Parâmetro de código de venda não pode ser nulo.|	Verificar implementação da chamada da biblioteca.|
   |1009 |	Parâmetro de resultado da transação não pode ser nulo.|	Verificar implementação da chamada da biblioteca.|
   |1010 |	Driver de conexão não encontrado.|	Verificar se todos os arquivos estão no diretório correto.|
   |1011 |	Erro ao utilizar driver de conexão.|	Reinstalar os arquivos do driver de conexão.|
   |1012 |	Formato do valor da venda inválido.|	Valor deve ser um número inteiro sem vírgula.|
   |1013 |	Comprimento do código de venda superior a 10 dígitos.|	Truncar código de venda para no máximo 10 dígitos.|
   |1014 |	Buffer de recepção corrompido.|	Refaça a transação.|
   |1015 |	Nome da aplicação maior que 25 caracteres.|	Limitar nome da aplicação a 25 caracteres.|
   |1016 |	Versão da aplicação maior que 10 caracteres.|	Limitar versão da aplicação em 10 caracteres.|
   |1017 |	Necessário definir nome da aplicação.|	Definir nome e versão da aplicação com setVersionName(String, String)|
   |1018 |	Não existem dados da última transação.|	Refaça a transação.|
   |1019 |	Erro de comunicação com terminal (resposta inesperada).|	Realizar consulta de última transação.|
   |1024 |	Erro na carga de tabelas.|	Refazer inicialização (carga de tabelas).|
   |1030 |	Token não encontrado|	Refazer autenticação.|
   |1031 |	Valor inválido|	Verificar o valor configurado para pagamento e tentar novamente. Valor mínimo: R$ 1,00|
   |1032 |	Parcelamento inválido|	Verificar o número de parcelas e tentar novamente.|
   |2001 |	Porta COM informada não encontrada.|	Informar uma porta COM válida.|
   |2002 |	Não foi possível obter configurações da porta COM informada.|	Informar uma porta COM válida.|
   |2003 |	Não foi possível configurar a porta COM informada.|	Informar uma porta COM válida.|
   |2005 |	Não foi possível enviar dados pela porta COM informada.|	Informar uma porta COM válida.|
   |2022 |	Java – Adaptador Null.|	Verificar implementação.|
   |2023 |	Java – erro em DeviceToUse.|	Coletar log (se existir) e enviar para o suporte.|
   |2024 |	Java – erro no serviço RfcommSocket.|	Coletar log (se existir) e enviar para o suporte.|
   |2026 |	Java – Close exception.|	Coletar log (se existir) e enviar para o suporte.|
   |3001 |	Permissão de root|	Remover permissão de root do aparelho.|
   |4046 |	Não existe dados de autenticação|	Efetuar a autenticação.|