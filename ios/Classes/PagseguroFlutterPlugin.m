#import "PagseguroFlutterPlugin.h"
#import <pagseguro_flutter/pagseguro_flutter-Swift.h>

@implementation PagseguroFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPagseguroFlutterPlugin registerWithRegistrar:registrar];
}
@end
