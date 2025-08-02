# Inohom Smart Home Systems

## Proje Özeti
Bu proje, akıllı ev otomasyonu için geliştirilen bir Android uygulamasıdır. Kullanıcılar evdeki aydınlatma, priz, perde ve benzeri sistemleri gerçek zamanlı olarak WebSocket üzerinden yönetebilir.

---

## Uygulama Akışı

1. **Kullanıcı Girişi (Login):**
   - Kullanıcıdan giriş bilgileri alınır.
   - WebSocket üzerinden authentication işlemi yapılır (kullanıcı adı/şifre ile).
   - Başarılı login sonrası ana menüye geçilir.

2. **Ana Menü ve Navigasyon:**
   - Kullanıcıya grid menü şeklinde tüm modüller (aydınlatma, perde vs) sunulur.
   - İlgili modüle tıklandığında ilgili fragment’a geçiş yapılır.

3. **Gerçek Zamanlı Kontrol (Aydınlatma/Lighting):**
   - LightingFragment açıldığında WebSocket ile "GetControlList" mesajı gönderilir.
   - Gelen cihaz (lamba) listesi bir grid üzerinde gösterilir.
   - Bir cihaza tıklanırsa "UpdateControlValue" mesajı ile aç/kapa işlemi yapılır.
   - Backend’den gelen "OnEntityUpdated" eventleri ile arayüz anlık güncellenir.

---

## WebSocket Kullanımı

- **WebSocketService.kt** singleton olarak çalışır, uygulamanın her yerinden erişilebilir.
- Tüm bağlantı ve mesaj yönetimi (bağlantı, authentication, kontrol ve event dinleme) burada yapılır.
- Sunucu adresi:  
  `SERVER_URL = "wss://64.227.77.73:9095/ws"`  
==> Server_url bağlantı hatası aldığım için doğru url le değiştirilmesi gerektedir. Akışın kontrolünü aydınlatmaya kadar echo serverla test ederek tamamladım.
- Girişte authentication mesajı:
    ```json
    {
      "id": 8,
      "is_request": true,
      "method": "Authenticate",
      "params": [{"username":"admin", "password":"admin"}]
    }
    ```
 ==> username ve password bilgilerinin de doğru kullanıcı adı ve şifreyle değiştirilmesi gerekir.   
- Aydınlatma cihaz listesi almak için:
    ```json
    {
      "is_request": true,
      "id": 5,
      "params": [{}],
      "method": "GetControlList"
    }
    ```
- Cihaz aç/kapa işlemi için:
    ```json
    {
      "is_request": true,
      "id": 84,
      "params": [
        {"id": "<cihaz-id>", "value": 1}
      ],
      "method": "UpdateControlValue"
    }
    ```

---

## Dosya ve Katman Açıklamaları
- Projeyi **MVVM Mimarisi**ne uygun olacak şekilde tasarlandı. Proje organizasyonu ve view- viewmodel arasındaki bağlantının daha sağlıklı kurulması sağlandı.

- **LoginFragment & LoginViewModel:** Giriş ve authentication akışı.
- **HomeFragment:** Grid menü ve modül navigation.
- **LightingFragment:** Cihaz listesi çekimi ve anlık kontrol.
- **WebSocketService:** Tüm bağlantı ve mesajlaşmanın merkezi.
- **LightingAdapter:** RecyclerView üzerinde dinamik cihaz UI’sı.

---

## Kullanılan Teknolojiler & Bağımlılıklar

- **Kotlin**
- **Android Jetpack** (ViewModel, LiveData, Navigation, Fragment, ConstraintLayout)
- **Hilt** (dependency injection)
- **Kotlin Coroutines & Flow**
- **org.java-websocket** (v1.5.3)
- **Gson** (v2.10.1)
- Detaylı versiyonlar için: [gradle/libs.versions.toml](gradle/libs.versions.toml)

---

## Geliştirici Notları

- Kodun önemli noktalarında adım adım Türkçe açıklayıcı yorumlar mevcuttur.
- WebSocket bağlantısı için sunucuya dış ağdan erişim, port ve SSL ayarlarının açık olması gerekir.
- `SERVER_URL`, kullanıcı adı ve şifre **`Constants.kt`** üzerinden ayarlanır.
- Yerleşik StateFlow altyapısı sayesinde UI otomatik olarak güncellenir.

---

## Geliştirilebilecek Özellikler

- Uygulamaya dinamik componentler tasarlayarak Anaekran üzerindeki tüm özelliklerin kontrolü daha temiz bir kod yapısıyla sağlanabilir.
- Uygulama içerisine dark/light mode özellikleri eklenebilir.
- Uygulama içerisine İngilizce dil desteği sağlanabilir.
