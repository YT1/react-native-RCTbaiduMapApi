//
//  BNUIManagerProtocol.h
//  baiduNaviSDK
//
//  Created by Baidu on 11/10/13.
//  Copyright (c) 2013 baidu. All rights reserved.
//

#ifndef baiduNaviSDK_BNUIManagerProtocol_h
#define baiduNaviSDK_BNUIManagerProtocol_h

#import "BNCommonDef.h"

extern NSString* BNaviUI_NormalNavi_TypeKey;

typedef enum _BNaviUIType{
    BNaviUI_Unknown = 0,
    BNaviUI_NormalNavi,         //正常导航
    BNaviUI_Declaration,       //声明页面
}BNaviUIType;

/**
 退出UI的两种方式
 EN_BNavi_ExitTopVC：
 退出最顶层的ViewController，如果退出后已经是最底部的controller，退出整个导航组件
 EN_BNavi_ExitAllVC：
 退出整个导航组件
 */
typedef enum _BNavi_ExitPage_Type {
    EN_BNavi_ExitTopVC,
    EN_BNavi_ExitAllVC
    
}BNavi_ExitPage_Type;

@protocol BNNaviUIManagerDelegate;

@protocol BNUIManagerProtocol

@required

/**
 * 导航页面的页面栈
 */
- (id)navigationController;

/**
 * 展示导航页面
 * @param pageType  需要展示的页面类型
 * @param delegate  回调代理
 * @param extParams 可扩展参数，传nil即可
 */
- (void)showPage:(BNaviUIType)pageType
        delegate:(id<BNNaviUIManagerDelegate>)delegate
       extParams:(NSDictionary*)extParams;

/**
 * 退出导航页面
 * @param exitType  退出页面的类型，参照BNavi_ExitPage_Type类型说明
 * @param animated  是否需要展示动画
 * @param extraInfo 可扩展参数，传nil即可
 */
- (void)exitPage:(BNavi_ExitPage_Type)exitType animated:(BOOL)animated extraInfo:(NSDictionary *)extraInfo;

@optional

/**
 *  是否在导航过程页面
 */
- (BOOL)isInNaviPage;

@end


// 导航UI管理器回调
@protocol BNNaviUIManagerDelegate <NSObject>

@optional

/**
 *  导航页面的调起controller
 *  可不实现，默认为最上层的controller
 */
- (id)naviPresentedViewController;

/**
 *  退出UI的回调
 *
 *  @param pageType  UI类型
 *  @param extraInfo 额外参数
 */
- (void)onExitPage:(BNaviUIType)pageType  extraInfo:(NSDictionary*)extraInfo;

@end

#endif
