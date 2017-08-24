#iOS
//定位
1、导入项目react-native-RCTbaiduMapApi
2、info.plist中添加（以下二选一，两个都添加默认使用NSLocationWhenInUseUsageDescription）：
             NSLocationWhenInUseUsageDescription ，允许在前台使用时获取GPS的描述
             NSLocationAlwaysUsageDescription ，允许永久使用GPS的描述
 3、基础配置参考百度地图官网基础配置http://lbsyun.baidu.com/index.php?title=iossdk/guide/buildproject
 4、要使用百度地图，请先启动BaiduMapManager
    1>AppDelegate.m中导入#import "RCTBaiDuApi.h"
    2>在- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
    方法中添加  [[[RCTBaiDuApi alloc] init]regestBaiDuMapApi:@"百度地图SDK"];
5、定位使用
  import BaiduMapApi from 'react-native-RCTbaiduMapApi';

    componentDidMount(){
     this.removeListener = DeviceEventEmitter.addListener("callBack",(event)=>{
                //latitude: event.latitude,
                //longitude: event.longitude,
                //address: event.address,
            });
    BaiduMapApi.startLocation({},);
    }
     componentWillUnmount(){
                this.removeListener.remove();
            }
     //停止定位
        BaiduMapApi.stopLocation();
  6、导航的配置
  在项目中添加配置
  AudioToolbox.framework、ImageIO.framework、CoreMotion.framework、CoreLocation.framework、CoreTelephony.framework、MediaPlayer.framework、
  AVFoundation.framework、SystemConfiguration.framework、JavaScriptCore.framework、Security.framework 、OpenGLES.framework 、GLKit.framework 、libstdc++6.0.9.dylib、libc++.tbd、libsqlite3.0.tbd、libz.1.2.5.tbd
  这几个framework添加到工程中，添加方法为在Xcode中单击工程文件，选择Build Phrases选项，点击Link Binary with Libraries下的“+”逐个添加。另外thirdlibs中的两个静态库libssl.a、libcrypto.a也要添加到工程中

  设置 “Required background modes”、 “App Transport Security Settings”（可以根据自身情况设置，导航sdk已经全面支持https）、
  ”NSLocationAlwaysUsageDescription”、 ”NSLocationWhenInUseUsageDescription”、 ”View controller-based status bar appearance”、
  ” LSApplicationQueriesSchemes”
  7、AppDelegate.m 中#import "BNCoreServices.h"
    //初始化导航SDK
      [BNCoreServices_Instance initServices: NAVI_TEST_APP_KEY];
      [BNCoreServices_Instance startServicesAsyn:nil fail:nil];
   8、使用导航功能
    //导航
        BaiduMapApi.startNavigation({
                            'startlongitude':this.state.longitude,
                            'startlatitude':this.state.latitude,
                            'endlongitude':this.state.target.longitude,
                            'endlatitude':this.state.target.latitude
                        });
#Android

1. 添加项目引用，在android/setting.gradle中:


    include ':react-native-RCTbaiduMapApi'
    project(':react-native-RCTbaiduMapApi').projectDir = new File(rootProject.projectDir,'../node_modules/react-native-RCTbaiduMapApi/android')

2.在android/app/build.gradle中：

    dependencies {

    compile fileTree(dir: "libs", include: ["*.jar"])

    compile project(':react-native-RCTbaiduMapApi')//百度地图（定位、导航、地图）
    }


3. 添加package:
*  使用startReactApplication




     //MainActivity
    import android.os.Bundle;
    import com.facebook.react.ReactInstanceManager;
    import com.facebook.react.ReactRootView;
    import com.facebook.react.shell.MainReactPackage;

    import com.dowin.baidumap.BaiduMapPackage;//百度地图（定位、导航、地图）
    import com.baidu.mapapi.SDKInitializer;


    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

     @Override
     protected void onCreate(Bundle savedInstanceState) {

        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                 .setApplication(getApplication())
                 .setBundleAssetName("index.android.bundle")
                 .setJSMainModuleName("index.android")
                 .addPackage(new MainReactPackage())
                .addPackage(new BaiduMapPackage(this))//百度地图（定位、导航、地图）
                 .setUseDeveloperSupport(true)
                 .setInitialLifecycleState(LifecycleState.RESUMED)
                 .build();

        Bundle options = new Bundle();
        //
        mReactRootView.startReactApplication(mReactInstanceManager, getMainComponentName(), options);
     }



*  setReactNativeHost


     import android.os.Bundle;
     import com.facebook.react.ReactInstanceManager;
     import com.facebook.react.ReactRootView;
     import com.facebook.react.shell.MainReactPackage;

     import com.dowin.baidumap.BaiduMapPackage;//百度地图（定位、导航、地图）
     import com.baidu.mapapi.SDKInitializer;


     import com.facebook.react.ReactNativeHost;
     import com.facebook.react.ReactPackage;
     import java.util.Arrays;
     import java.util.List;

     @Override
     protected void onCreate(Bundle savedInstanceState) {

         //初始化百度地图
         SDKInitializer.initialize(getApplicationContext());
         super.onCreate(savedInstanceState);
         
         MainApplication application = (MainApplication) getApplication();
         application.setReactNativeHost(new ReactNativeHost(application) {
             @Override
             protected boolean getUseDeveloperSupport() {
                 return false;
             }
         
             @Override
             protected List<ReactPackage> getPackages() {
                 return Arrays.<ReactPackage>asList(
                         new MainReactPackage(),
                         new BaiduMapPackage(MainActivity.this),//百度地图（定位、导航、地图）
                 );
             }
         });
     }
     //MainApplication
     public void setReactNativeHost(ReactNativeHost mReactNativeHost) {
         this.mReactNativeHost = mReactNativeHost;
     }


4. AndroidManifest.xml


    <meta-data
        android:name="com.baidu.lbsapi.API_KEY"
        android:value="4vHZGvGTA2anny1znG0p2GK30lkwGg0T" />


#使用


    import BaiduMapApi from 'react-native-RCTbaiduMapApi';

    componentDidMount(){
     this.removeListener = DeviceEventEmitter.addListener("callBack",(event)=>{
                //latitude: event.latitude,
                //longitude: event.longitude,
                //address: event.address,
            });
    BaiduMapApi.startLocation({},);
    }
    componentWillUnmount(){
            this.removeListener.remove();
        }
    //导航
    BaiduMapApi.startNavigation({
                        'startlongitude':this.state.longitude,
                        'startlatitude':this.state.latitude,
                        'endlongitude':this.state.target.longitude,
                        'endlatitude':this.state.target.latitude
                    });
    //停止定位
    BaiduMapApi.stopLocation();
    
#