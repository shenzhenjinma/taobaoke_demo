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

import {HorsePush} from 'NativeModules';
exports.initLanguage = () => {
    HorsePush.getLanguage((lan)=> {
        exports.language = lan;
    });
};

exports.language= '';



