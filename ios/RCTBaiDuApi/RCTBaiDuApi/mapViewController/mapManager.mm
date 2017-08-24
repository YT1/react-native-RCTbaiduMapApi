//
//  mapManager.m
//  RCTBuDuMap
//
//  Created by Dowin on 16/11/30.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import "mapManager.h"

@implementation mapManager{
    BMKMapManager* _mapManager;
    BMKLocationService *_locService;
}

+(instancetype)initShare{
    static mapManager *maneger = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        maneger = [[mapManager alloc]init];
    });
    return maneger;
}
-(void)mapLoaction{
    //初始化BMKLocationService
    _locService = [[BMKLocationService alloc]init];
    _locService.delegate = self;
    //启动LocationService
    [_locService startUserLocationService];
}
-(void)regestMapAK:(NSString *)AK finish:(faceSuccess)BlockPra{
    
    // 要使用百度地图，请先启动BaiduMapManager
    _mapManager = [[BMKMapManager alloc]init];
    BOOL ret = [_mapManager start:AK generalDelegate:self];
    if (!ret) {
        BlockPra(@"manager start failed!");
    }else{
        BlockPra(@"成功");
    }
}
-(void)stopLocation{

    [_locService stopUserLocationService];

}
//线路规划
-(void)waysetAddress:(NSString *)dictionAdress  andController:(UIViewController *)controller finish:(faceSuccess)BlockPra
{
    WayViewController *wayVC = [[WayViewController alloc]init];
    wayVC.view.backgroundColor = [UIColor whiteColor];
    wayVC.destionAddress = dictionAdress;
    UINavigationController *navi = [[UINavigationController alloc] initWithRootViewController:wayVC];
    [controller presentViewController:navi animated:YES completion:^{
        
    }];
     BlockPra(@"成功");
}
//开启导航
-(void)waysetAddressID:(NSMutableDictionary *)diction andController:(UIViewController *)controller finish:(faceSuccess)BlockPra
{
//    WayViewController *wayVC = [[WayViewController alloc]init];
//    wayVC.view.backgroundColor = [UIColor whiteColor];
//    wayVC.destionAddress = dictionAdress;
//    UINavigationController *navi = [[UINavigationController alloc] initWithRootViewController:wayVC];
//    [controller presentViewController:navi animated:YES completion:^{
//        
//    }];
    
    
    NSMutableArray *nodesArray = [[NSMutableArray alloc]initWithCapacity:2];
    //起点 传入的是原始的经纬度坐标，若使用的是百度地图坐标，可以使用BNTools类进行坐标转化
    BNRoutePlanNode *startNode = [[BNRoutePlanNode alloc] init];
    startNode.pos = [[BNPosition alloc] init];
    startNode.pos.x = [[diction objectForKey:@"startlongitude"] floatValue];
    startNode.pos.y = [[diction objectForKey:@"startlatitude"] floatValue];
    startNode.pos.eType = BNCoordinate_BaiduMapSDK;
    [nodesArray addObject:startNode];
    
    //终点
    BNRoutePlanNode *endNode = [[BNRoutePlanNode alloc] init];
    endNode.pos = [[BNPosition alloc] init];
    endNode.pos.x = [[diction objectForKey:@"endlongitude"] floatValue];;
    endNode.pos.y = [[diction objectForKey:@"endlatitude"] floatValue];
    endNode.pos.eType = BNCoordinate_BaiduMapSDK;
    [nodesArray addObject:endNode];
    
    [BNCoreServices_RoutePlan setDisableOpenUrl:YES];
    [BNCoreServices_RoutePlan startNaviRoutePlan:BNRoutePlanMode_Recommend naviNodes:nodesArray time:nil delegete:self userInfo:nil];
    
    BlockPra(@"成功");
}

//实现相关delegate 处理位置信息更新
//处理方向变更信息
- (void)didUpdateUserHeading:(BMKUserLocation *)userLocation
{
//    NSLog(@"++++++++++++++heading is %@",userLocation.location.coordinate.latitude);
 
//    self.bloc([NSString stringWithFormat:@"%f",userLocation.location.coordinate.latitude],[NSString stringWithFormat:@"%f",userLocation.location.coordinate.longitude]);

    
    
    
}
//处理位置坐标更新
- (void)didUpdateBMKUserLocation:(BMKUserLocation *)userLocation
{
    NSLog(@"*************didUpdateUserLocation lat %f,long %f",userLocation.location.coordinate.latitude,userLocation.location.coordinate.longitude);
    
       self.bloc([NSString stringWithFormat:@"%f",userLocation.location.coordinate.latitude],[NSString stringWithFormat:@"%f",userLocation.location.coordinate.longitude]);
}


#pragma mark - BNNaviRoutePlanDelegate
//算路成功回调
-(void)routePlanDidFinished:(NSDictionary *)userInfo
{
    NSLog(@"算路成功");
    //设置为外部gps导航模式
    [BNCoreServices_Location setGpsFromExternal:YES];
    //显示导航UI
    [BNCoreServices_UI showPage:BNaviUI_NormalNavi delegate:self extParams:nil];
    //开始发送gps
    [self.externalGPSModel startPostGPS];
}

//算路失败回调
- (void)routePlanDidFailedWithError:(NSError *)error andUserInfo:(NSDictionary *)userInfo
{
    NSLog(@"算路失败");
}

//算路取消回调
-(void)routePlanDidUserCanceled:(NSDictionary*)userInfo {
    NSLog(@"算路取消");
}

#pragma mark - BNNaviUIManagerDelegate

//退出导航页面回调
- (void)onExitPage:(BNaviUIType)pageType  extraInfo:(NSDictionary*)extraInfo
{
    [_externalGPSModel stopPostGPS];
    if (pageType == BNaviUI_NormalNavi)
    {
        NSLog(@"退出导航");
    }
    else if (pageType == BNaviUI_Declaration)
    {
        NSLog(@"退出导航声明页面");
    }
}

/**
 *定位失败后，会调用此函数
 *@param mapView 地图View
 *@param error 错误号，参考CLError.h中定义的错误号
 */
- (void)didFailToLocateUserWithError:(NSError *)error
{

    self.errorBloc([NSString stringWithFormat:@"%@",error]);
//    if (error.code == 1) {
//        [self alertViewAction];
//    }
    
}
- (void)alertViewAction{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message:@"请先在手机设置-隐私-定位服务-里面打开该应用权限" delegate:nil cancelButtonTitle:@"知道了" otherButtonTitles:nil];
    [alert show];
    
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
