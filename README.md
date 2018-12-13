# 关于
虹软人脸识别SDK之Java版，支持SDK 1.1+，以及当前最新版本2.0，滴滴，抓紧上车！

![JDK](https://img.shields.io/badge/JDK-1.8-green.svg)
![SDK](https://img.shields.io/badge/SDK-2.0-brown.svg)
![Win](https://img.shields.io/badge/windows-x64-yellow.svg)
![release](https://img.shields.io/badge/release-2.0.0-red.svg)
![license](https://img.shields.io/badge/license-MIT-blue.svg)
![status](https://img.shields.io/badge/status-prd-brightgreen.svg)


# 前言
由于业务需求，最近跟人脸识别杠上了，本以为虹软提供的SDK是那种面向开发语言的，结果是一堆dll······像我这样的Java猿突然就感觉整个人都不好了；近期赶上了SDK2.0的升级，在官方论坛、Google、百度、QQ等等的帮助下，爬过了一个又一个坑，终于搞定了！噗！回过头来发现不少伙伴们都像我当初一样迷茫，So，我回来拯救世界了~


# 注意
- 本项目需要lombok的支持
- 虹软官方分为“人证SDK”、“活体检测”和“ArcFace”三种，本项目使用的是“ArcFace”
- Windows 32位、64位指的是JDK，并不是Windows操作系统的位数
- master分支为最新的SDK 2.0版本，1.x的版本实现请查看[1.x分支](https://github.com/jastar-wang/arcface/tree/feature/1.x)（1.x实现来自于官方论坛Demo）
- 本项目实现了对SDK的轻度封装，且为单线程模式，多线程情况下尚未测试（或不适用），求饶...


# 目前功能
- [x] 激活引擎：已封装并测试通过
- [x] 初始化引擎：已封装并测试通过
- [x] 检测人脸：已封装并测试通过
- [x] 提取特征值：已封装并测试通过
- [x] 对比特征值：已封装并测试通过
- [ ] 人脸年龄/性别/角度检测预处理
- [ ] 获取年龄
- [ ] 获取性别
- [ ] 获取3D角度
- [x] 获取引擎版本：已封装并测试通过
- [x] 销毁引擎：已封装并测试通过


# 快速开始
## 安装项目
```
git clone https://github.com/jastar-wang/arcface.git
```
然后将项目导入到Eclipse或IDEA中并进行maven update

## 下载DLL
登录[虹软官方网站](http://ai.arcsoft.com.cn/ucenter/user/userlogin)，下载ArcFace 2.0的SDK，并将`libarcsoft_face.dll`和`libarcsoft_face_engine.dll`两个文件粘贴到`src/test/resources`目录下

## 配置KEY
将官网获取到的SDK激活码填入到`com.arcsoft.face.util.ConfUtil`类的对应常量中

## 测试
打开`com.arcsoft.face.EngineTest`类，运行单元测试即可


# 参考资料
- [虹软SDK的常见问题指南](http://ai.arcsoft.com.cn/manual/faqs.html)
- [虹软官方问答指导集锦](https://ai.arcsoft.com.cn/bbs/forum.php?mod=viewthread&tid=884&extra=page%3D1)

# 常见问题
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

（8）如何加载图片？
> 答：可以使用“ImageIO.read()方式读取图片，但该方式读取某些类型的图片时，不会正确读取ICC的信息，因此会在写出图片时生成一层红色蒙版（如果不写出图片那就无所谓）。有两种解决方案：(1)参照EngineTest.testAll()方法中的加载方式，这种方式仅限于Windows下使用，因为需要用到图形环境的支持。(2)使用javacv（基于opencv）加载图片，pom依赖中已注释，本人放弃的原因是因为jar包太大且需求不会有Linux环境。”

（9）运行`EngineTest`单元测试提示“...找不到指定模块”？
> 答：这是因为你的系统缺少dll运行的必要组件，别担心，点击 [这里](https://download.csdn.net/download/qq_16313365/10849250) 进行下载，根据自身情况选择32位或64位安装即可。

# 许可证
本项目遵循 [MIT](https://mit-license.org/) 开源协议，手动比心:blush: