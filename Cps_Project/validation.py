from flask import Flask, jsonify, request
import numpy as np
import PIL
from PIL import Image
from keras.models import load_model
import tensorflow as tf

from flask_cors import CORS, cross_origin

app = Flask(__name__)   
CORS(app)
model = load_model('frozenModel.h5')
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
graph = tf.get_default_graph()

gestures_arr = ['Door Opening' , 'Microwave Opening']

@app.route('/recognizer', methods=["POST"])
def predict_image():
        video = request.files.to_dict()
        video = video['input_video']
        video = Image.open(video)
        video = np.asarray(video.resize((256,256)))
        video = video.reshape(1,256,256,3)        
        global graph
        with graph.as_default():
            pred = model.predict_classes(video)
        predicted_gesture = gestures_arr[pred[0][0]]
        return jsonify(predicted_gesture)

@app.route('/testing', methods=["GET"])
def test():
        predicted_gesture={'success' : True}
        return jsonify(predicted_gesture)



if __name__ == "__main__":
       app.run(host='172.24.21.147')