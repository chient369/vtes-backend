### 概要

VTESバックエンドシステムは、Java言語で書かれており、ユーザーが日付別のチケットの情報を検索し、Excelファイルでチケットの情報をエクスポートすることを目的としています。

- 交通情報ソース：[Navitime Japan株式会社](https://api-sdk.navitime.co.jp/api/specs/)
- 接続方法：中間者の[RapidAPI](https://rapidapi.com/search/navitime)を介して接続する
- 使用中のエンドポイント:
- NAVITIME Route（totalnavi）：列車のチケット価格情報を取得する
- NAVITIME Transport：場所や駅の情報を取得する

フレームワーク：Springフレームワーク

ツール：

- Docker：MySQLサーバー、Redisサーバー、およびアプリケーションを構成および初期化するために使用されます。
    - バージョン：23.0.5
- Redis：呼び出されたすべての駅の情報をキャッシュサーバーに保存するために使用されます。
    - バージョン：7.0
    - 設定：メモリ500 MB
    - 最大メモリポリシー：allkeys-lfu
    - 保存期間：30日
- Mysql：データベースサーバ
    - バージョン：8.0.33

### ライブラリ：

- Feign（Spring Cloud）-サードパーティAPIに接続してチケット情報を取得するためのもの
- Java Mail sender-ユーザーに情報を送信するためのメールサーバーを作成するためのもの
- AWS-S3-SDK：Amazon S3に接続し、アップロード、ファイルのダウンロードを実行するためのもの
- Data JPA：DBへの迅速な接続とデータの要求を容易にするためのもの
- Spring security：JWTを使用してユーザーのログインセッションを保護するためのもの
- Jackson：オブジェクトからJSONデータに変換するためのもの

### インストール手順：

1. Dockerをインストールしてください。
- Windowsの場合：[Install Docker Desktop on Windows | Docker Documentation](https://docs.docker.com/desktop/install/windows-install/)
- Linuxの場合：[Install Docker Engine on Ubuntu | Docker Documentation](https://docs.docker.com/engine/install/ubuntu/)
1. GitLab VTIからプロジェクトをクローンします。
    
    `git clone https://git.vti.com.vn/chien.tranvan/vtes-backend.git`
    
2. vtes-backendフォルダに移動して、コマンドラインを開き、次を実行します。
- `docker compose up`

### 使用手順：

1. リンクにアクセスしてください：http://localhost:8080
2. ユーザーアカウントに登録またはログインしてください。
3. 検索してファイルをエクスポートしてください。

### 使用されるエンドポイント：

- ‘POST api/v1/auth/login’: システムにログインする
- ‘POST api/v1/auth/register’: 新規登録-アカウントを作成する
- ‘GET api/v1/auth/logout’: システムからログアウトする
- ‘POST api/v1/auth/refreshToken’: アクセストークンを更新する
- ‘PUT api/v1/users: ユーザー情報を更新する
- ‘POST api/v1/emails: パスワードを忘れたユーザーはメールアドレスを入力して送信する
- ‘POST api/v1/users/reset-password’: 新しいパスワードを設定する
- ‘GET api/v1/stations’: 駅情報を検索する
- ‘GET api/v1/routes’: チケット価格情報およびルート情報を検索する
- ‘POST api/v1/fares’: 入力したチケット情報をサーバーにアップロードする
- ‘DELETE api/v1/fares/{id}’: 入力したチケット情報を削除する
- ‘GET api/v1/users/files’: エクスポート済みのファイル情報を取得する
- ‘POST api/v1/files’: エクスポート済みのファイル情報をサーバーに送信する
- ‘GET api/v1/users’: 現在のユーザー情報を取得する
- ‘GET api/v1/users/active’: アカウントをアクティブにする
- ‘GET api/v1/departments’: 会社内の部署情報を取得する
- ‘GET api/v1/cp-routes’: 月額チケット情報を取得する
- ‘GET api/v1/files/{fileId}’: エクスポートしたファイルをダウンロードする

### ディレクトリ構造

```
├─src
│  └─main
│      ├─java
│      │  └─com
│      │      └─vtes
│      │          ├─config
│      │          ├─controller
│      │          ├─entity
│      │          ├─exception
│      │          ├─model
│      │          │  └─navitime
│      │          ├─payload
│      │          ├─repository
│      │          ├─security
│      │          │  ├─jwt
│      │          │  └─service
│      │          └─service
│      └─resources
├─Dockerfile
├─docker-compose.yml
├─redis.conf
└─vtesdb
```
- src/main/java: アプリケーションの主要なクラスが含まれています。
    - /config: アプリケーションの設定ファイルが含まれています。
    - /controller: コントローラーが含まれています。リクエストを受信し、クライアントにデータを返します。
    - /entity: データベースとデータのやり取りをするエンティティオブジェクトが含まれています。
    - /exception: アプリケーション内で発生した例外エラークラスが含まれています。
    - /model: サービス層とリポジトリ層の間で交換するためのDTO（Data Transfer Object）が含まれています。
        - /navitime:
            - Navitimeからのレスポンスで返されるデータオブジェクトが含まれています。
            - 目的：Javaオブジェクトに変換して処理し、クライアントに返します。
    - /payload: ブラウザーとサーバー間で交換されるrリクエストのデータが含まれています。
    - /repository: データベースとデータのやり取りをするためのクラスが含まれています。
    - /security: アプリケーションのセキュリティを設定します。
        - /jwt: JWT（Json Web Token）の使用設定を含むクラスが含まれています。
        - /service: セキュリティロジックを処理するためのクラスが含まれています。
    - /service: アプリケーションのロジックを処理するためのクラスが含まれています。
- src/main/resource: アプリケーションの環境設定ファイルが含まれています。
- Dockerfile: Docker内の仮想環境を作成し、アプリケーションをビルドするための手順を定義します。
- docker-compose.yml: システムに必要な環境を定義し、作成するための設定を定義します。Redisサーバー、MySQLサーバー、Vtes-Applicationを含みます。
- redis.config: Redisサーバーの設定ファイル
- /vtesdb: Docker内でデータベースを初期化するためのSQLファイルが含まれています。

### 作成者

- Tran Van Chien (VJP)
- メール：[chien.tranvan@vti.com.vn](mailto:chien.tranvan@vti.com.vn)
- Nguyen Thanh Cong (VJP)
- メール：[cong.nguyenthanh2@vti.com.vn](mailto:cong.nguyenthanh2@vti.com.vn)

### お問い合わせ：

ご質問、提案、またはフィードバックがある場合は、以下のメールアドレスにお問い合わせください：[chien.tranvan@vti.com.vn](mailto:chien.tranvan@vti.com.vn)
