# 实验六 多 Fragment 应用

李逸凡 2024110518

一个基于 `Fragment` + `BottomNavigationView` 的 Android 应用，包含三个可切换的 Fragment：

1. **个人信息**：展示姓名、性别、学号、电话、邮箱和爱好，支持在对话框中修改。
2. **两张牌游戏**：24 张牌，每次翻开两张点数相同的牌即可配对。
3. **三张牌游戏**：24 张牌，每次翻开三张点数相同的牌即可配对。

三个 Fragment 都使用 Java 对象序列化（`Serializable` + `ObjectOutputStream` / `ObjectInputStream`）把状态写入应用私有文件，退出应用后重新打开可以恢复之前的界面状态：

| Fragment | 状态文件 | 保存内容 |
| --- | --- | --- |
| 个人信息 | `player_info.dat` | `PlayerInfo` 对象 |
| 两张牌游戏 | `game_two.dat` | `CardMatchingGame` 对象 |
| 三张牌游戏 | `game_three.dat` | `CardMatchingGame` 对象 |

## 功能说明

- 底部导航栏在三个 Fragment 之间切换，使用 `FragmentTransaction.replace` 完成。
- 个人信息修改后立即保存，离开界面时（`onStop`）也会再次保存。
- 游戏使用 `RecyclerView` + `GridLayoutManager` 展示卡牌，竖屏 4 列、横屏 6 列。
- 配对规则：点数相同得分 `4`，花色相同得分 `1`；每选一张牌扣 `1` 分，配对失败额外扣 `2` 分。只有点数相同的牌才会被锁定为已配对。
- “重置游戏”重新发牌并清空得分。

## 主要代码

```
app/src/main/java/com/example/a2024110518/
├── MainActivity.kt              # 底部导航 + Fragment 切换
├── BaseGameFragment.kt          # 两个游戏的公共基类（发牌、计分、序列化）
├── TwoCardGameFragment.kt       # 两张牌游戏
├── ThreeCardGameFragment.kt     # 三张牌游戏
├── PersonInfoFragment.kt        # 个人信息
├── CardAdapter.kt               # 卡牌 RecyclerView 适配器
└── model/
    ├── Card.kt                  # 单张牌，实现 Serializable
    ├── CardMatchingGame.kt      # 牌局模型，实现 Serializable
    └── PlayerInfo.kt            # 个人信息模型和文件存储
```

## 运行

用 Android Studio 打开工程，或使用命令行：

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

debug 包的 applicationId 为 `com.example.a2024110518.lab6debug`，避免与模拟器上其他实验工程冲突；release 包仍为 `com.example.a2024110518`。

## 分支说明

- `main`：主分支，保存最终可运行版本。
- `Dev`：开发分支，汇总开发过程中的改动。
- `Frag`：Fragment 功能开发分支。
