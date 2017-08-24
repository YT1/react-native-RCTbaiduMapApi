//
//  RCTBaiDuApi.m
//  RCTBaiDuApi
//
//  Created by Dowin on 16/12/6.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import "RCTBaiDuApi.h"
#import "RCTUtils.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"
@implementation RCTBaiDuApi
{
    BMKMapManager *_mapManager;
}
@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();
-(void)regestBaiDuMapApi:(NSString *)AK
{
    [[mapManager initShare]regestMapAK:AK finish:^(NSString *s) {
        NSLog(@"%@",s);
    }];
}

//定位
RCT_EXPORT_METHOD(openMapLocation:(nonnull NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    [[mapManager initShare] mapLoaction];
    [mapManager initShare].bloc = ^(NSString *s1,NSString *s2){
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"callBack" body:@{@"latitude":s1,@"longitude":s2}];
        
    };
    [mapManager initShare].errorBloc = ^(NSString *error){
     reject(@"-1",error, nil);
    };
  resolve(@"成功");
}
//结束定位
RCT_EXPORT_METHOD(stopLocation:(nonnull NSDictionary *)options)
{

    [[mapManager initShare] stopLocation];
}
//传入地址，经过定位反编译实现导航功能
RCT_EXPORT_METHOD(openBaiDuLocationDetect:(nonnull NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
    UIViewController *controller = RCTKeyWindow().rootViewController;
    while (controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    [[mapManager initShare]waysetAddress:[options objectForKey:@"address"] andController:controller finish:^(NSString *v) {
        resolve(v);
    }];
    
}
//导航
RCT_EXPORT_METHOD(openBaiDuNavigationDetect:(nonnull NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
    UIViewController *controller = RCTKeyWindow().rootViewController;
    while (controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    [[mapManager initShare]waysetAddressID:options andController:controller finish:^(NSString *v) {
        
        resolve(v);
    }];
    
}
- (void)onGetNetworkState:(int)iError
{
    if (0 == iError) {
        NSLog(@"联网成功");
    }
    else{
        NSLog(@"onGetNetworkState %d",iError);
    }
    
}

- (void)onGetPermissionState:(int)iError
{
    if (0 == iError) {
        NSLog(@"授权成功");
    }
    else {
        NSLog(@"onGetPermissionState %d",iError);
    }
}


@end
