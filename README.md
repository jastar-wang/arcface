# 关于
虹软人脸识别SDK之Java版，支持SDK 1.1+，以及当前最新版本2.0，看什么，抓紧上车！

![JDK](https://img.shields.io/badge/JDK-1.8-green.svg)
![SDK](https://img.shields.io/badge/SDK-2.0-brown.svg)
![Win](https://img.shields.io/badge/windows-x64-yellow.svg)
![license](https://img.shields.io/badge/license-MIT-blue.svg)
![status](https://img.shields.io/badge/status-dev-brightgreen.svg)
![release](https://img.shields.io/badge/release-1.0.0-red.svg)


# 前言
由于业务需求，本人跟这个玩意挂上了边，本以为虹软提供的SDK是那种面向开发语言的，结果是一堆dll·····像我这样的Java猿突然就感觉整个人都不好了；近期还赶上了SDK2.0的重大升级，在各种论坛、Google、百度、QQ等等的帮助下，爬过了一个又一个坑，终于搞定了！噗！回过头来发现不少伙伴们都像我当初一样迷茫，So，我回来拯救世界了，当当当当~

# 许可证
本项目遵循 [MIT](https://mit-license.org/) 开源协议

# 快速开始
## 下载DLL文件
## 配置KEY
## 测试

# 参考资料
- [虹软SDK的常见问题指南](http://ai.arcsoft.com.cn/manual/faqs.html)

# 注意事项
（1）SDK有效期？
> 答：SDK激活码有效期为一年，到期之后需要重新申请激活码，并且重新下载SDK，SDK与激活码应当匹配。

（2）SDK 2.0首次使用？
> 答：2.0版本首次使用需要联网激活，激活后会在程序运行目录下生成几个“.dat”文件（asf_install.dat,freesdk_数字.dat），之后则可以离线使用；若更换了机器设备，需要删除这些“.dat”文件，并重新联网激活。

（3）SDK 2.0特征值？
> 答：1.x的版本特征值大小为20k左右，2.0版本算法做了大幅优化，缩小到了1k，且长度固定为1023 byte。

（4）SDK 2.0对于图片的要求？
> 答：图片的宽度必须是4的整数倍；当图片为BGR模式时，高度需要是2的整数倍。

（5）SDK 2.0的阈值？
> 答：由于2.0的算法做了优化，匹配度相对较高，推荐阈值设置为0.8（1.x版本为0.6），具体根据实际场景上下调整。

（6）SDK 2.0提取特征值？
> 答：2.0版本对内存做了优化，会反复使用同一个特征值对象的内存，所以提取特征值之后需要对特征值对象重新copy，否则SDK再进行提取时会覆盖了原特征值。

（7）关于多线程使用？
> 答：多线程使用场景下，每个线程都需要初始化一个引擎。
