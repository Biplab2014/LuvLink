package com.luv.link.repositories.mqtt

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.luv.link.global.data.GlobalData.macId
import com.luv.link.global.data.GlobalData.mqttBrokerUrl
import com.luv.link.logger.NibLogger
import java.io.InputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Calendar
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.security.cert.X509Certificate
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttRepository(
    context: Context,
    private val logger: NibLogger
) {
    private val tag = "MqttRepository"
    private val initial = "Nibble_Mqtt_"
    private var mqttClient: MqttAndroidClient? = null
    private var clientId: String? = null

    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> get() = _connectionStatus

    private val _incomingMqttMessages = MutableLiveData<Pair<String, String>>()
    val incomingMqttMessages: LiveData<Pair<String, String>> get() = _incomingMqttMessages

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    private var mqttConnectOptions: MqttConnectOptions = MqttConnectOptions()

    init {
        logger.debug(tag, "==============> MqttRepository init")
        mqttConnectOptions.socketFactory = getSSLSocketFactory(context)
        generateClientId()
        if (mqttClient == null) {
            mqttClient = MqttAndroidClient(context.applicationContext, mqttBrokerUrl, clientId)
        }
        // Set callback for MQTT client to handle various events like message arrival,
        // connection loss, etc.
        mqttClient?.setCallback(
            object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    _connectionStatus.postValue(false)
                    _errorMessage.postValue("Connection lost: ${cause?.message}")
                }

                override fun messageArrived(
                    topic: String?,
                    message: MqttMessage?
                ) {
                    topic?.let {
                        _incomingMqttMessages.postValue(
                            Pair(
                                it,
                                String(
                                    message?.payload
                                        ?: ByteArray(0)
                                )
                            )
                        )
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Called when a message is delivered successfully
                    // This is useful if we want to track delivery success (optional for most apps)
                }
            }
        )
    }

    private fun generateClientId() {
        val calendar: Calendar = Calendar.getInstance()
        clientId = initial + macId + "_" + calendar.timeInMillis
        logger.debug(tag, "clientId == $clientId")
    }

    fun getClient(): MqttAndroidClient {
        if (mqttClient == null) throw IllegalStateException("GlobalMqttClient not initialized!")
        return mqttClient!!
    }

    // Connect to the MQTT broker
    fun connect() {
        val options =
            MqttConnectOptions().apply {
                isCleanSession = true
                // Optional: Set username/password if needed
                // setUserName("your_username")
                // setPassword("your_password".toCharArray())
            }

        try {
            mqttClient?.connect(
                options,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        _connectionStatus.postValue(true)
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken?,
                        exception: Throwable?
                    ) {
                        _connectionStatus.postValue(false)
                        _errorMessage.postValue("Connection failed: ${exception?.message}")
                    }
                }
            )
        } catch (e: MqttException) {
            _errorMessage.postValue("Error while connecting: ${e.message}")
        }
    }

    // Subscribe to a specific topic
    fun subscribe(
        topic: String,
        qosValue: Int
    ) {
        if (mqttClient?.isConnected == true) {
            mqttClient?.subscribe(topic, qosValue)?.actionCallback =
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        // Subscription successful
                    }

                    override fun onFailure(
                        asyncActionToken: IMqttToken?,
                        exception: Throwable?
                    ) {
                        _errorMessage.postValue("Subscription failed: ${exception?.message}")
                    }
                }
        } else {
            _errorMessage.postValue("Cannot subscribe, client is not connected.")
        }
    }

    // Publish a message to a specific topic
    fun publish(
        topic: String,
        message: String
    ) {
        if (mqttClient?.isConnected == true) {
            try {
                val mqttMessage = MqttMessage(message.toByteArray())
                mqttClient
                    ?.publish(
                        topic,
                        mqttMessage
                    ) ?.actionCallback =
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            // Message sent successfully
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            _errorMessage.postValue("Publish failed: ${exception?.message}")
                        }
                    }
            } catch (e: MqttException) {
                _errorMessage.postValue("Error while publishing: ${e.message}")
            }
        } else {
            _errorMessage.postValue("Cannot publish, client is not connected.")
        }
    }

    // Disconnect from the MQTT broker
    fun disconnect() {
        if (mqttClient?.isConnected == true) {
            try {
                mqttClient?.disconnect(
                    null,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            _connectionStatus.postValue(false)
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            _errorMessage.postValue(
                                "Disconnection failed: " +
                                    "${exception?.message}"
                            )
                        }
                    }
                )
            } catch (e: MqttException) {
                _errorMessage.postValue("Error while disconnecting: ${e.message}")
            }
        }
    }

    // Reconnect the MQTT client
    fun reconnect() {
        if (!mqttClient?.isConnected!!) {
            connect()
        } else {
            _errorMessage.postValue("Client is already connected.")
        }
    }

    private fun getSSLSocketFactory(context: Context): SSLSocketFactory? =
        try {
            val caInput: InputStream = context.assets.open("ca.pem")
            val certInput: InputStream = context.assets.open("client_cert.pem")
            val keyInput: InputStream = context.assets.open("client_key.pem")

            // Load the CA certificate
            val certFactory = CertificateFactory.getInstance("X.509")
            val caCertificate = certFactory.generateCertificate(caInput) as X509Certificate

            // Load the client certificate
            val clientCertificate = certFactory.generateCertificate(certInput) as X509Certificate

            // Load the client private key (PKCS#8 format)
            val keyBytes = keyInput.readBytes()
            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes)) as ByteArray

            // Set up the KeyStore with the client cert and private key
            val keyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(null, null)

            // Store the private key and certificate in the keystore
            keyStore.setCertificateEntry("client-cert", clientCertificate as Certificate)
            keyStore.setKeyEntry("client-key", privateKey, null)

            // Initialize KeyManagerFactory with the keystore
            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(keyStore, null)

            val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
            trustStore.load(null, null) // Initialize an empty trust store
            trustStore.setCertificateEntry("ca-cert", caCertificate as Certificate)

            // Initialize TrustManagerFactory with the CA certificate
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(trustStore)

            // Set up the SSLContext with the KeyManager and TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(kmf.keyManagers, tmf.trustManagers, null)

            sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}
