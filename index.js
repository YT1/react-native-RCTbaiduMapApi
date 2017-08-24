'use strict';
import React, {
    Component,
    PropTypes,
} from 'react';
import { View,NativeModules,requireNativeComponent,Platform } from 'react-native';
const BaiduLocation = NativeModules.RNBaiduLocation;
const BaiduApi = undefined;
if(Platform.OS === 'ios'){
    BaiduApi = NativeModules.BaiDuApi;
}else{
   BaiduApi = NativeModules.BaiduApi;
}
export default class BaiduMapApi{
    static startLocation(value){
        //开始定位
        BaiduApi.openMapLocation(value).then(r=>{
            console.info('定位成功',r);
        },e=>{
            console.info('定位失败',e);
        })
    }
    static stopLocation(){
        //停止定位
        BaiduApi.stopLocation({});
    }
    static startMap(value){
        //Ios定位导航
        BaiduApi.openBaiDuLocationDetect(value).then(r=>{
        },e=>{
        })
    }
    static  startNavigation(value){
        if(Platform.OS === 'ios'){
            //导航
            BaiduApi.openBaiDuNavigationDetect(value).then(r=>{
            },e=>{
            })
        }else{
            BaiduApi.openBaiduNavigationDetect(value).then(r=>{

            },e=>{

            });
        }

    }

}

export class BaiduMap extends Component{

    constructor(props) {
        super(props);
        this.onLocation = this.onLocation.bind(this);
        this.onChange = this.onChange.bind(this);
    }
    _assignRoot = (component) => {
        this._root = component;
    };

    static requestLocation = (callback) => {
        BaiduLocation.requestLocation(callback);
    }
    onLocation = (event) => {
        if(this.props.onLocation){
            this.props.onLocation(event.nativeEvent);
        }
    }
    onChange = (event) => {
        if(this.props.onChange){
            this.props.onChange(event.nativeEvent);
        }
    }

    render (){

        const nativeProps = Object.assign({}, this.props);
        Object.assign(nativeProps, {
            onLocation:this.onLocation,
            onChange:this.onChange
        });
        return (
            <RCTBaiDuApi
                ref={this._assignRoot}
                {...nativeProps}/>
        );
    }
}

BaiduMap.propTypes = {
    ...View.propTypes,
    onLocation:PropTypes.func,
    init:PropTypes.bool,
    mode: PropTypes.number,
    span: PropTypes.number,
    location: PropTypes.object,
    trafficEnabled: PropTypes.bool,
    heatMapEnabled: PropTypes.bool,
    marker:PropTypes.array
}

const RCTBaiduMap = requireNativeComponent('RCTBaiduMap', BaiduMap);
