import flask
from flask import request, jsonify, abort
from flask_cors import CORS, cross_origin
import keywords
import json

keywords_service = keywords.KeywordsProcessor()

app = flask.Flask(__name__)
CORS(app)

@app.route('/', methods=['GET'])
def home():
    return '''<h1>librAIry NLP</h1>
<p>A prototype API for NLP tasks.</p>'''


@app.route('/nlp', methods=['GET'])
def nlphome():
    return '''<h1>librAIry NLP</h1>
<p>A prototype API for NLP tasks:
    <ul>
      <li>/keywords</li>
    </ul>
</p>'''


@app.route('/nlp/keywords', methods=['POST'])
def post_keywords():
    if not request.json or not 'text' in request.json:
        abort(400)
    keywords = keywords_service.get_keywords(request.json['text'])
    return jsonify(keywords)

@app.route('/nlp/keywords', methods=['GET'])
def get_keywords():
    if not 'text' in request.args:
        abort(400)
    keywords = keywords_service.get_keywords(request.args.get('text'))
    return jsonify(keywords)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
