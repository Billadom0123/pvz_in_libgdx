import os

def to_mp3(input_path, output_path):
    if not os.path.exists(input_path):
        raise FileNotFoundError(f"Input file {input_path} does not exist.")

    command = f'ffmpeg -i "{input_path}" -vn -ar 44100 -ac 2 -b:a 192k "{output_path}"'
    os.system(command)

if __name__ == "__main__":
    for file in os.listdir('.'):
        if file.endswith('.ogg'):
            mp3_file = file.rsplit('.', 1)[0] + '.mp3'
            to_mp3(file, mp3_file)