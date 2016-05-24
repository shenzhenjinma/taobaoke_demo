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

import Configs from './RConfigs';
let getString = (key)=> {
    let language = Configs.language;
    let languagePackage;
    try {
        switch (language) {
            case "en":
                languagePackage = require("./string/en");
                break;
            case "in":
                languagePackage = require("./string/in");
                break;
            default:
                languagePackage = require("./string/zh");
        }
    }
    catch (e) {
        languagePackage = require("./string/zh");
    }
    return languagePackage[key];
};
module.exports = key=>getString(key);
