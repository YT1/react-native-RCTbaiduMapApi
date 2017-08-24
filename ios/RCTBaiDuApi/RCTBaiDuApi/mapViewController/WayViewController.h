//
//  WayViewController.h
//  RCTBuDuMap
//
//  Created by Dowin on 16/11/30.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <BaiduMapAPI_Map/BMKMapComponent.h>
#import <BaiduMapAPI_Search/BMKSearchComponent.h>
#import <BaiduMapAPI_Location/BMKLocationComponent.h>
#import <BaiduMapAPI_Utils/BMKUtilsComponent.h>
#import "BNRoutePlanModel.h"
#import "BNCoreServices.h"
#import "ExternalGPSModel.h"
@interface WayViewController : UIViewController<BMKGeneralDelegate,BMKMapViewDelegate,BMKLocationServiceDelegate,BMKGeoCodeSearchDelegate,BMKRouteSearchDelegate,BNNaviUIManagerDelegate,BNNaviRoutePlanDelegate>
@property (nonatomic,strong) BMKMapView *mapView;
@property (nonatomic,strong) BMKLocationService *locService;
@property (nonatomic,strong) BMKGeoCodeSearch *searcher;
@property (nonatomic,strong) BMKRouteSearch* routesearch;
@property (nonatomic,assign) CLLocationDegrees latitude;
@property (nonatomic,assign) CLLocationDegrees longitude;
@property(nonatomic,strong)NSString *destionAddress;
//外部gps产生model
@property (strong, nonatomic) ExternalGPSModel* externalGPSModel;
@end
