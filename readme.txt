naver : https://developers.naver.com/main
kakao : https://developers.kakao.com
sk : https://developers.skplanetx.com

googledev : innocnscjh@gmail.com / 15880046

innocns / i15880046
package name 변경 해야함. 현재는 com.entereal.parking
com.innocns.parking


네이버맵을 사용하기 위해 naver 계정이 필요함. dev 가입 해야함
    client id

release.keystore 가 필요함
innocns.keystore
key store password : i15880046
key alias : innocns
key password : i15880046

최근 키 적용

guardzone.jks 인증키
key store password : 15880046!
key alias : key0
key password : 15880046!



kakao dev 계정이 필요함
    네이티브 앱 키가 필요함
    kakao_strings.xml -> kakao_app_key 값을 넣어야 함

    해시키도 생성 해야함

    디버그 해시키 생성 방법
    기본적으로 debug.keystore는 alias가 androiddebugkey 임
    keytool -exportcert -alias androiddebugkey -keystore c:\keystore\debug.keystore -storepass android -keypass android | openssl sha1 -binary | openssl base64 > debug.txt

    릴리즈 해시키 생성 방법
    https://developers.kakao.com/docs/android#시작하기-앱-생성
    keytool -exportcert -alias <release_key_alias> -keystore <release_keystore_path> | openssl sha1 -binary | openssl base64 > innocns_release.txt

    해시키 생성 할 경우 주의사항
    https://devtalk.kakao.com/t/topic/623 참고해서
    https://code.google.com/archive/p/openssl-for-windows/downloads 여기에서
    openssl-0.9.8e_X64 e 버전을 다운받자. k 버전을 다운 받으면 안된다는 사람들이 많다. 예외로 그 반대의 경우도 있다


sk dev 계정이 필요함
    https://developers.skplanetx.com 가입 후에
    1. 앱 추가
    2. 소스코드 TMAP_APPKEY 변경
    3. sk dev에서 앱 선택 후 서비스 관리 이동
    4. T map 관련 서비스 체크
    5. 나중에 서비스 할 경우 SLA 관리에서 SLA 등급 변경(상용으로) 그래야 일일 api 회수가 증가됨