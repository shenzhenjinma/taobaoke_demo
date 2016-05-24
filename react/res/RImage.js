//                              _(\_/)
//                            ,((((^`\
//                           ((((  (6 \
//                         ,((((( ,    \
//     ,,,_              ,(((((  /"._  ,`,
//    ((((\\ ,...       ,((((   /    `-.-'
//    )))  ;'    `"'"'""((((   (
//   (((  /            (((      \
//    )) |                      |
//   ((  |        .       '     |
//   ))  \     _ '      `t   ,.')
//   (   |   y;- -,-""'"-.\   \/
//   )   / ./  ) /         `\  \
//      |./   ( (           / /'
//      ||     \\          //'|
//      ||      \\       _//'||
//      ||       ))     |_/  ||
//      \_\     |_/          ||
//      `'"                  \_\
//                           `'"
//
// order:       黄泽彬
// email:       root@68xg.com
// website:     http://68xg.com

'use strict';
import React from 'react-native'
import Configs from './RConfigs';
let getImage = (key)=> {
    let language = Configs.language;
    let languagePackage = require("./image/zh");
    try {
        switch (language) {
            case "en":
                languagePackage = require("./image/en");
                break;
            case "in":
                languagePackage = require("./image/in");
                break;
        }
    }
    catch (e) { }
    return languagePackage[key];
};

let {Animated,} = React;
class RImage extends React.Component {


    render() {
        let { style, inputRange, outputRange,duration,uri } = this.props;
        inputRange = inputRange || [0, 100];
        outputRange = outputRange || [0, 1];
        duration = duration || 500;
        style = style || {height: 100, width: 100};
        let source = !uri ||  {uri: getImage(uri)};

        this._animatedValue = new Animated.Value(0);
        let interpolatedColorAnimation = this._animatedValue.interpolate({
            inputRange: inputRange,
            outputRange: outputRange
        });
        return (
            <Animated.Image
                onLoadEnd={() => {
                Animated.timing(this._animatedValue, {
                toValue: 100,
                duration: duration
                }).start();
                }}
                {...this.props}
                source={source}
                style={[style, {opacity: interpolatedColorAnimation}]}/>
        )
    }
}

module.exports = RImage;