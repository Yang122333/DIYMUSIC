package com.example.yang.diymusic.audio;

import android.content.Context;

import com.example.yang.diymusic.R;
import com.example.yang.diymusic.model.PressInformation;

import java.io.File;
import java.util.List;

public class AudioService {
    private static int PIANO_AUDIO_NUMBER = 88;
    private Context context;
    private String ELEMENT_AUDIO_PATH;
    private String EMPTY_AUDIO_PATH;
    public String MUSIC;

    public int[] getPianoIds() {
        return pianoIds;
    }

    private int[] pianoIds = new int[PIANO_AUDIO_NUMBER];
    private String[] pianoPaths = new String[PIANO_AUDIO_NUMBER];

    private static volatile AudioService singleton;

    public static AudioService getInstance(Context context) {
        if (singleton == null) {
            synchronized (AudioService.class) {
                if (singleton == null) {
                    singleton = new AudioService(context);
                }
            }
        }
        return singleton;
    }

    private AudioService() {
    }

    private AudioService(Context context) {
        this.context = context;
        String basePath = context.getFilesDir().getPath();
        ELEMENT_AUDIO_PATH = basePath + File.separator + "piano";
        EMPTY_AUDIO_PATH = basePath + File.separator + "emptyaudio";
        MUSIC = basePath + File.separator + "music";
        initPianoId();
    }

    public void produceAudio(String musicName, List<PressInformation> informations, long startTime) {
        if (informations == null) {
            return;
        }
        //写入元素音频
        initElementAudio();
        //生成根据所需时间生成空音频
        Audio mAudio = new Audio();
        mAudio.setChannel(2);
        mAudio.setBitNum(16);
        mAudio.setSampleRate(44100);
        //duration 为最后一个按键时间减去第一个按键时间然后加上10s 因为音频最长时间为9s
        long duration = informations.get(informations.size() - 1).getPressTime()
                - startTime + 10 * 1000;
        mAudio.setDuration(duration);
        mAudio.setPcmPath(EMPTY_AUDIO_PATH + File.separator + musicName + "temp" + ".pcm");
        mAudio.setWavPath(MUSIC + File.separator + musicName + ".wav");
        initEmptyAudio(mAudio);
        compoundAudio(mAudio, informations, startTime);
    }

    public void handleAudio(String srcMusicName ,String armMusicName, List<PressInformation> informations, long startTime){
        initElementAudio();
        srcMusicName = MUSIC + File.separator + srcMusicName ;
        armMusicName = MUSIC + File.separator + armMusicName + ".wav";

        FileUtil.copyFile(srcMusicName,armMusicName);
        try {
            Audio mAudio = Audio.createAudioFromFile(new File(armMusicName));
            compoundAudio(mAudio, informations, startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void initElementAudio() {
        for (int i = 0; i < PIANO_AUDIO_NUMBER; i++) {
            int index = i + 1;
            pianoPaths[i] = ELEMENT_AUDIO_PATH + File.separator + "piano" + index + ".wav";
        }
        if (FileUtil.judeAndCreateDirExists(new File(ELEMENT_AUDIO_PATH))) {
            return;
        }
        for (int i = 0; i < PIANO_AUDIO_NUMBER; i++) {
            FileUtil.writeToFileFromRaw(context, pianoIds[i], pianoPaths[i]);
        }
    }

    private void initPianoId() {
        pianoIds[0] = R.raw.piano01a;
        pianoIds[1] = R.raw.piano02ab;
        pianoIds[2] = R.raw.piano03b;
        pianoIds[3] = R.raw.piano04c;
        pianoIds[4] = R.raw.piano05cb;
        pianoIds[5] = R.raw.piano06d;
        pianoIds[6] = R.raw.piano07db;
        pianoIds[7] = R.raw.piano08e;
        pianoIds[8] = R.raw.piano09f;
        pianoIds[9] = R.raw.piano10fb;
        pianoIds[10] = R.raw.piano11g;
        pianoIds[11] = R.raw.piano12gb;
        pianoIds[12] = R.raw.piano13a;
        pianoIds[13] = R.raw.piano14ab;
        pianoIds[14] = R.raw.piano15b;
        pianoIds[15] = R.raw.piano16c;
        pianoIds[16] = R.raw.piano17cb;
        pianoIds[17] = R.raw.piano18d;
        pianoIds[18] = R.raw.piano19db;
        pianoIds[19] = R.raw.piano20e;
        pianoIds[20] = R.raw.piano21f;
        pianoIds[21] = R.raw.piano22fb;
        pianoIds[22] = R.raw.piano23g;
        pianoIds[23] = R.raw.piano24gb;
        pianoIds[24] = R.raw.piano25a;
        pianoIds[25] = R.raw.piano26ab;
        pianoIds[26] = R.raw.piano27b;
        pianoIds[27] = R.raw.piano28c;
        pianoIds[28] = R.raw.piano29cb;
        pianoIds[29] = R.raw.piano30d;
        pianoIds[30] = R.raw.piano31db;
        pianoIds[31] = R.raw.piano32e;
        pianoIds[32] = R.raw.piano33f;
        pianoIds[33] = R.raw.piano34fb;
        pianoIds[34] = R.raw.piano35g;
        pianoIds[35] = R.raw.piano36gb;
        pianoIds[36] = R.raw.piano37a;
        pianoIds[37] = R.raw.piano38ab;
        pianoIds[38] = R.raw.piano39b;
        pianoIds[39] = R.raw.piano40c;
        pianoIds[40] = R.raw.piano41cb;
        pianoIds[41] = R.raw.piano42d;
        pianoIds[42] = R.raw.piano43db;
        pianoIds[43] = R.raw.piano44e;
        pianoIds[44] = R.raw.piano45f;
        pianoIds[45] = R.raw.piano46fb;
        pianoIds[46] = R.raw.piano47g;
        pianoIds[47] = R.raw.piano48gb;
        pianoIds[48] = R.raw.piano49a;
        pianoIds[49] = R.raw.piano50ab;
        pianoIds[50] = R.raw.piano51b;
        pianoIds[51] = R.raw.piano52c;
        pianoIds[52] = R.raw.piano53cb;
        pianoIds[53] = R.raw.piano54d;
        pianoIds[54] = R.raw.piano55db;
        pianoIds[55] = R.raw.piano56e;
        pianoIds[56] = R.raw.piano57f;
        pianoIds[57] = R.raw.piano58fb;
        pianoIds[58] = R.raw.piano59g;
        pianoIds[59] = R.raw.piano60gb;
        pianoIds[60] = R.raw.piano61a;
        pianoIds[61] = R.raw.piano62ab;
        pianoIds[62] = R.raw.piano63b;
        pianoIds[63] = R.raw.piano64c;
        pianoIds[64] = R.raw.piano65cb;
        pianoIds[65] = R.raw.piano66d;
        pianoIds[66] = R.raw.piano67db;
        pianoIds[67] = R.raw.piano68e;
        pianoIds[68] = R.raw.piano69f;
        pianoIds[69] = R.raw.piano70fb;
        pianoIds[70] = R.raw.piano71g;
        pianoIds[71] = R.raw.piano72gb;
        pianoIds[72] = R.raw.piano73a;
        pianoIds[73] = R.raw.piano74ab;
        pianoIds[74] = R.raw.piano75b;
        pianoIds[75] = R.raw.piano76c;
        pianoIds[76] = R.raw.piano77cb;
        pianoIds[77] = R.raw.piano78d;
        pianoIds[78] = R.raw.piano79db;
        pianoIds[79] = R.raw.piano80e;
        pianoIds[80] = R.raw.piano81f;
        pianoIds[81] = R.raw.piano82fb;
        pianoIds[82] = R.raw.piano83g;
        pianoIds[83] = R.raw.piano84gb;
        pianoIds[84] = R.raw.piano85a;
        pianoIds[85] = R.raw.piano86ab;
        pianoIds[86] = R.raw.piano87b;
        pianoIds[87] = R.raw.piano88c;

    }

    private void initEmptyAudio(Audio mAudio) {
        FileUtil.judeAndCreateDirExists(new File(EMPTY_AUDIO_PATH));
        FileUtil.judeAndCreateDirExists(new File(MUSIC));
        FileUtil.writeEmptyAudio(mAudio);
        Convert.convertPcm2Wav(mAudio);
    }

    private void compoundAudio(Audio audio, List<PressInformation> informations, long startTime) {
        if (audio == null || informations == null) {
            return;
        }
        for (PressInformation info : informations) {
            AudioEditUtil.compoundAudio(audio, pianoPaths[info.getButtonId()], info.getPressTime() - startTime);

        }
    }
}
