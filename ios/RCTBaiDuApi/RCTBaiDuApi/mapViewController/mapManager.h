//
//  mapManager.h
//  RCTBuDuMap
//
//  Created by Dowin on 16/11/30.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <BaiduMapAPI_Base/BMKBaseComponent.h>//引入base相关所有的头文件

#import <BaiduMapAPI_Map/BMKMapComponent.h>//引入地图功能所有的头文件

#import <BaiduMapAPI_Search/BMKSearchComponent.h>//引入检索功能所有的头文件

#import <BaiduMapAPI_Cloud/BMKCloudSearchComponent.h>//引入云检索功能所有的头文件

#import <BaiduMapAPI_Location/BMKLocationComponent.h>//引入定位功能所有的头文件

#import <BaiduMapAPI_Utils/BMKUtilsComponent.h>//引入计算工具所有的头文件
#import "WayViewController.h"

typedef void(^faceSuccess)(NSString *v);
typedef void(^Blo)(NSString *s1,NSString *s2);
typedef void(^Error)(NSString *s1);
@interface mapManager : NSObject<BMKGeneralDelegate,BNNaviUIManagerDelegate,BNNaviRoutePlanDelegate,BMKLocationServiceDelegate>
@property (nonatomic, strong) Blo bloc;
@property(nonatomic,strong)Error errorBloc;
+(instancetype)initShare;
//开始定位
-(void)mapLoaction;
//结束定位
-(void)stopLocation;
//注册
-(void)regestMapAK:(NSString *)AK finish:(faceSuccess)BlockPra;
//线路规划
-(void)waysetAddress:(NSString *)dictionAdress andController:(UIViewController *)controller finish:(faceSuccess)BlockPra;
//导航
-(void)waysetAddressID:(NSMutableDictionary *)diction andController:(UIViewController *)controller finish:(faceSuccess)BlockPra;
@property (strong, nonatomic) ExternalGPSModel* externalGPSModel;
@end
