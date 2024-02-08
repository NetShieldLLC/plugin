import shutil
import glob
import os

files_to_copy = os.path.join("../", "**", "NetShield-*-*.jar")

for file in glob.glob(files_to_copy, recursive=True):
    if "-shaded.jar" not in file:
        print("Copied: "+file)
        shutil.copy(file, ".")