from flask import Flask, request, Response
from transformers import AutoTokenizer, AutoModelForCausalLM
import torch
import argparse
import json

app = Flask(__name__)
model = None
tokenizer = None

# You can choose the model here (ensure it's available locally or via Hugging Face)
MODEL = "google/gemma-3-1b-it"
# MODEL = "meta-llama/Llama-3.2-1B"  # Alternate option

def prepareLlamaBot():
    global model, tokenizer
    print(f"Loading {MODEL} model... This may take a while.")

    tokenizer = AutoTokenizer.from_pretrained(MODEL)
    tokenizer.pad_token = tokenizer.eos_token if tokenizer.pad_token is None else tokenizer.pad_token

    model = AutoModelForCausalLM.from_pretrained(
        MODEL,
        # device_map="auto",
        # torch_dtype=torch.float16 if torch.cuda.is_available() else torch.float32
    )

    print("Model and tokenizer loaded successfully.")

@app.route('/')
def index():
    return "Welcome to the Llama Chatbot API!"

@app.route('/chat', methods=['POST'])
def chat():
    global model, tokenizer

    user_message = request.form.get('userMessage') or request.get_data(as_text=True).strip()

    if not user_message:
        return Response("Error: userMessage cannot be empty", status=400, mimetype='text/plain')

    print(f"\nReceived Request: {user_message}")

    prompt = user_message
    inputs = tokenizer(prompt, return_tensors="pt", truncation=True, max_length=512, padding=True)
    if torch.cuda.is_available():
        inputs = {k: v.cuda() for k, v in inputs.items()}

    try:
        with torch.no_grad():
            outputs = model.generate(
                input_ids=inputs['input_ids'],
                attention_mask=inputs['attention_mask'],
                max_new_tokens=100,
                min_new_tokens=1,
                do_sample=True,
                top_p=0.85,
                temperature=0.6,
                pad_token_id=tokenizer.pad_token_id,
                no_repeat_ngram_size=2
            )
        raw_output = tokenizer.decode(outputs[0], skip_special_tokens=True).strip()
        if raw_output.startswith(prompt):
            raw_output = raw_output[len(prompt):].strip()
    except Exception as e:
        print(f"Error during generation: {str(e)}")
        raw_output = ""

    print(f"Raw Model Output: {raw_output}")

    # === Clean Markdown, JSON, and common wrapping issues ===
    if raw_output.startswith("```") and raw_output.endswith("```"):
        raw_output = raw_output.strip("`").strip()

    if raw_output.lower().startswith("json"):
        raw_output = raw_output[4:].strip()

    if raw_output.startswith('{') and raw_output.endswith('}'):
        try:
            raw_output = json.loads(raw_output).get("response", raw_output)
        except:
            pass

    response = raw_output

    if not response or response.isspace() or len(response.split()) < 3 or len(set(response.split())) < len(response.split()) * 0.7:
        response = f"Sorry, I couldn't provide a relevant answer to: '{user_message}'. Please rephrase."

    print(f"Generated Response: {response}\n")
    return Response(response, mimetype='text/plain')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=5001, help='Specify the port number')
    args = parser.parse_args()

    prepareLlamaBot()
    print(f"App running on port {args.port}")
    app.run(host='0.0.0.0', port=args.port)
