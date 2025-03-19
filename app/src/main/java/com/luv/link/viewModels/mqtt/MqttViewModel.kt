package com.luv.link.viewModels.mqtt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.luv.link.logger.NibLogger
import com.luv.link.repositories.mqtt.MqttRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MqttViewModel
@Inject
constructor(
    application: Application,
    private val logger: NibLogger
) : AndroidViewModel(application) {
    private val mqttRepository = MqttRepository(application, logger)
    private val tag = "MqttViewModel"
    val connectionStatus: LiveData<Boolean> = mqttRepository.connectionStatus
    val mqttMessage: LiveData<Pair<String, String>> = mqttRepository.incomingMqttMessages

    fun connect() {
        mqttRepository.connect()
        // logger.loge("hgh")
    }

    fun subscribe(topic: String) {
        mqttRepository.subscribe(topic, 1)
    }

    fun publishMessage(
        topic: String,
        message: String
    ) {
        logger.debug(tag, "publishMessage called")
        mqttRepository.publish(topic, message)
    }

    fun disconnect() {
        mqttRepository.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        mqttRepository.disconnect()
    }
}
