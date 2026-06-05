# CNN-Mobile

Este projeto implementa um sistema completo de **segmentação semântica de imagens**, desde o treinamento de redes neurais até a implantação em um aplicativo Android utilizando TensorFlow Lite.


# Objetivo

Treinar modelos de segmentação semântica (U-Net / FCN) e implantá-los em um smartphone Android, permitindo inferência em tempo real sobre imagens capturadas ou carregadas pelo usuário.



# Pipeline do Projeto

O fluxo completo do projeto é:

---

# Dataset
Utilizado o dataset:

- Oxford-IIIT Pet Dataset
- Segmentação semântica pixel-a-pixel
- Classes:
  - Pet (foreground)
  - Background
  - Border

---

# Modelos Implementados

##  U-Net (Simple Version)

- Encoder-decoder com skip connections
- Features: [64, 128]
- Output: 3 classes por pixel

## FCN-ResNet-like (comparativo)

- Modelo totalmente convolucional
- Avaliação comparativa com U-Net

---

# Métricas de Avaliação

Os modelos foram avaliados com:

- IoU (Intersection over Union)
- Accuracy pixel-wise
- TorchMetrics

### Resultados:

| Modelo     | IoU médio | Accuracy |
|------------|----------|----------|
| U-Net      | 0.45     | 0.60     |
| FCN        | 0.64     | 0.75     |

---

# Conversão do Modelo

Os modelos foram exportados para:

## ONNX
- formato intermediário para interoperabilidade

## TensorFlow Lite

### Modelos gerados:

- `model.tflite` → FP32
- `model_int8.tflite` → quantizado

---

# Aplicação Android

Aplicativo desenvolvido em **Kotlin + TensorFlow Lite Interpreter**.

## Funcionalidades:

- Captura de imagem via câmera
- Upload de imagem da galeria
- Seleção entre modelos FP32 e INT8
- Inferência local no dispositivo
- Visualização da segmentação

---

#  Inferência

O modelo retorna uma máscara com 3 classes:

- 0 → Background (preto)
- 1 → Pet (verde)
- 2 → Border (vermelho)


---

