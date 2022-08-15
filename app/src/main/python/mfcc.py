from java import jclass, jarray, jfloat
import librosa
import numpy as np
import _multiprocessing
from skimage.transform import resize
_multiprocessing.sem_unlink = None

def mfcc(wavfile):
    fix_n_sec = 2
    mini_sec =0.3
    num_channels = 3
    window_sizes = [25, 50, 100]
    hop_sizes = [10, 25, 50]
    feature_size=128
    y=[]
    try:
        y, sr = librosa.load(wavfile)
    except:
        print("read error: ",wavfile)

    if len(y)>0:
        # pad if len less 1 sec
        if len(y)/sr > fix_n_sec:
            x = y[:fix_n_sec * sr]
            run=1
        elif mini_sec < len(y)/sr < fix_n_sec :
            z = int(fix_n_sec*sr - len(y))
            if(z%2==0):
                z1 = int(z/2)
                z2 = int(z/2)
            else:
                z1 = int((z-1)/2)
                z2 = int(z1 + 1)
            try:
                x = np.pad(y,(z1,z2), 'linear_ramp', end_values=(0, 0))
                run=1
            except:
                print("pad error: ",wavfile)
            # ([0]*append_zero)+y+([0]*append_zero)
        elif len(y)/sr < mini_sec:
            print('len < '+mini_sec+': ',wavfile)
            run=0

        if run==1:
            # audio normalizedy
            normalizedy = librosa.util.normalize(x)
            specs = []
            for i in range(num_channels):

                window_length = int(round(window_sizes[i]*sr/1000))
                # print('win_len: ',window_length)
                hop_length = int(round(hop_sizes[i]*sr/1000))
                mel = librosa.feature.melspectrogram(
                    y=normalizedy, sr=sr,n_fft=window_length,hop_length=hop_length, win_length=window_length)
                mellog = np.log(mel + 1e-9)
                spec = librosa.util.normalize(mellog)
                spec = resize(mellog, (128, feature_size))
                spec = np.asarray(spec)
                specs.append(spec)

            # list to np array
            specs = np.asarray(specs)
            specs = np.moveaxis(specs,0,2)
            print('specs.shape : ',specs.shape)

        specs = specs.flatten()

        # specs = specs.float32(specs) #先註解掉試試
        JavaBean = jclass("com.chhsuan121.tflitepapere1.TFlite.JavaBean")
        jb = JavaBean()
        jb.setData(jarray(jfloat)(specs))
        return jb
