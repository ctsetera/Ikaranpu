# Playストア初回公開手順メモ

作成日: 2026-06-21

このメモは、Ikaranpu を Google Play に初回公開するための作業手順です。Play Console の画面やポリシー要件は変更されることがあるため、実作業時は Play Console と Android Developers の最新表示を優先してください。

## 全体の流れ

1. Play Console のデベロッパーアカウントを作成する
2. アプリ情報、プライバシーポリシー、ストア掲載素材を準備する
3. リリース用 Android App Bundle を作成する
4. Play Console でアプリを作成する
5. ポリシー、データセーフティ、対象年齢などの申告を完了する
6. 内部テストまたはクローズドテストで動作確認する
7. Production access が必要な場合は申請する
8. 本番リリースを作成して審査へ送信する

## 1. Play Console アカウント作成

Play Console の登録ページからデベロッパーアカウントを作成する。

- 登録ページ: https://play.google.com/console/signup
- 個人開発なら personal、法人またはチーム名義なら organization を選ぶ
- 本人確認、連絡先メール、電話番号、支払い情報を登録する
- 登録料が必要な場合は画面の案内に従って支払う

無料アプリとして公開したアプリは、あとから有料アプリに変更できない。課金が必要な場合は、初回公開前にアプリ内課金やサブスクリプションの方針を決めておく。

## 2. アプリ側の公開前確認

Android Studio 側で以下を確認する。

- `applicationId` が最終的な公開IDになっていること
- `versionCode` が過去のリリースより大きいこと
- `versionName` がユーザー向けのバージョン表記になっていること
- `targetSdk` が Google Play の最新要件を満たしていること
- リリースビルドでクラッシュしないこと
- リリース署名鍵を安全な場所に保管していること
- デバッグ用ログ、テスト用文言、開発用URLが残っていないこと

2026-06-21 時点では、新規アプリとアプリ更新は原則 Android 15 / API level 35 以上の target API level が必要。実際の提出時は公式要件を再確認する。

- Target API level requirements: https://support.google.com/googleplay/android-developer/answer/11926878

## 3. ストア掲載情報の準備

Play Console に入力する素材と説明文を用意する。

- アプリ名
- 短い説明
- 詳細説明
- アプリアイコン
- フィーチャーグラフィック
- スクリーンショット
- アプリカテゴリ
- 連絡先メールアドレス
- プライバシーポリシーURL

プライバシーポリシーは、リポジトリ内の `PRIVACY_POLICY.md` をベースに公開用URLを用意する。GitHub Pages、Webサイト、または公開リポジトリ上のページなど、ユーザーがブラウザでアクセスできるURLが必要。

## 4. リリース用 AAB の作成

Google Play への新規公開は Android App Bundle、つまり `.aab` を使用する。

Android Studio で作成する場合:

1. `Build` を開く
2. `Generate Signed Bundle / APK` を選ぶ
3. `Android App Bundle` を選ぶ
4. `release` ビルドバリアントを選ぶ
5. 署名鍵を指定して生成する

Gradle で作成する場合:

```powershell
.\gradlew bundleRelease
```

生成後、`app/build/outputs/bundle/release/` 配下の `.aab` を Play Console にアップロードする。

## 5. Play Console でアプリ作成

Play Console で `Create app` を選び、アプリを作成する。

- アプリ名
- デフォルト言語
- アプリまたはゲーム
- 無料または有料
- 各種宣言への同意

アプリ作成後、ダッシュボードに表示される未完了タスクを順番に埋める。

## 6. ポリシー項目の入力

Play Console の `App content` 周辺で、以下の項目を入力する。

- Data safety
- Content rating
- Target audience and content
- Ads
- App access
- Privacy policy
- News apps
- Health apps
- Financial features
- Government apps
- Sensitive permissions

該当しない項目は、該当しないことを正しく申告する。権限、SDK、外部送信、分析、クラッシュレポート、広告IDの扱いは Data safety とプライバシーポリシーの内容が矛盾しないようにする。

## 7. テストリリース

まず内部テストで AAB をアップロードし、自分の実機でインストールと基本操作を確認する。

確認する内容:

- 初回起動できる
- 主要画面に遷移できる
- 権限リクエストが自然に表示される
- オフライン時やエラー時に破綻しない
- ストア掲載文とアプリの実際の機能が一致している
- リリース版でクラッシュしない

新規の個人デベロッパーアカウントでは、本番公開前にクローズドテスト要件がある。2026-06-21 時点では、最低12人のテスターが14日間継続して参加する必要があるため、テスターの確保とスケジュールを先に準備する。

- App testing requirements: https://support.google.com/googleplay/android-developer/answer/14151465

## 8. Production access 申請

個人の新規デベロッパーアカウントで Production access が未解放の場合、クローズドテスト要件を満たしたあとに Play Console の Dashboard から申請する。

申請時に聞かれやすい内容:

- テストの実施内容
- テスターから得たフィードバック
- 修正した不具合
- 本番公開の準備状況
- アプリの目的と対象ユーザー

テスト中のフィードバック、修正履歴、既知の制限をメモしておくと申請しやすい。

## 9. 本番リリース

Production トラックで本番リリースを作成する。

1. リリース用 `.aab` をアップロードする
2. リリース名を確認する
3. リリースノートを書く
4. 警告や未完了項目を解消する
5. `Send for review` で審査に送信する

初回審査は数日以上かかることがある。審査中にポリシー違反や追加情報の要求が来た場合は、Play Console の通知内容に従って修正する。

## 初回公開前チェックリスト

- [ ] Play Console アカウントを作成した
- [ ] デベロッパー名と連絡先情報を確認した
- [ ] `applicationId` を確定した
- [ ] `versionCode` と `versionName` を設定した
- [ ] `targetSdk` が最新要件を満たしている
- [ ] リリース署名鍵を保管した
- [ ] プライバシーポリシーURLを用意した
- [ ] ストア掲載文を用意した
- [ ] アイコン、フィーチャーグラフィック、スクリーンショットを用意した
- [ ] Data safety の回答内容を整理した
- [ ] Content rating を入力した
- [ ] Target audience を入力した
- [ ] リリース用 `.aab` を作成した
- [ ] 内部テストで実機確認した
- [ ] 必要に応じてクローズドテストを完了した
- [ ] Production access を申請し、承認された
- [ ] Production リリースを審査へ送信した

