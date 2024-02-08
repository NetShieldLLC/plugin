import os

for root, dirs, files in os.walk("../"):
    for file in files:
        if file.endswith(".jar"):
            file_path = os.path.join(root, file)
            os.remove(file_path)
            print(f"Deleted: {file_path}")