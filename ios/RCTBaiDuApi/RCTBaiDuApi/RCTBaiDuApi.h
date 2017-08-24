//
//  RCTBaiDuApi.h
//  RCTBaiDuApi
//
//  Created by Dowin on 16/12/6.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"
#import "mapManager.h"
@interface RCTBaiDuApi : NSObject<RCTBridgeModule>
-(void)regestBaiDuMapApi:(NSString *)AK;
@end
