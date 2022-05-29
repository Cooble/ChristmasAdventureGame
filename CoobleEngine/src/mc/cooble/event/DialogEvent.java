package mc.cooble.event;

import mc.cooble.core.Game;
import mc.cooble.dialog.Dialog;
import mc.cooble.dialog.DialogUtil;
import mc.cooble.entity.Talkable;
import mc.cooble.graphics.dialog.DialogManager;
import mc.cooble.translate.Translator;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by Matej on 21.7.2017.
 */
public class DialogEvent implements Event {
    private final HashMap<String, Talkable> talkables = new HashMap<>();
    private final HashMap<String, Color> colors = new HashMap<>();
    private final Dialog dialog;
    private final DialogManager manager;
    private boolean fresh = true;
    private boolean canceled;

    private static final int PARAGRAPH_READTIME = 60;
    private static final int TEXT_LIFE = 30;

    private final String dialogName;

    private final String DIALOG_PATH;
    private Runnable onEnd;

    public DialogEvent(String dialogName) {
        this.dialogName = dialogName;
        DIALOG_PATH= Game.saver.DIALOG_PATH+dialogName+"/";
        dialogName = DIALOG_PATH+ "/dialog.xml";
        this.dialog = DialogUtil.loadDialog(dialogName);
        this.manager = Game.dialog;
        registerTalkable(Game.getWorld().getUniCreature(),"joe",Color.green);


    }

    public void registerTalkable(Talkable talkable, String name, Color color) {
        talkables.put(name, talkable);
        colors.put(name, color);
    }


    @Override
    public void dispatchEvent() {
        if (canceled)
            return;
        if (fresh) {
            dialog.start();
            fresh = false;
            Game.dialog.clearText();
        }
        muteTalking();
        DialogEvent dialogEvent = this;
        if (dialog.isMultipleComments()) {
            String[] before = DialogUtil.toArray(DialogUtil.getCurrentStrings(dialog));
            for (int i = 0; i < before.length; i++) {
                before[i] = Translator.translate("dialog." + dialogName + "." + before[i]);
            }
            manager.ask(before);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int answer = manager.waitForAnswer();
                    if (answer > -1) {//successful answer
                        dialog.choose(answer);
                        Game.core.EVENT_BUS.addEvent(dialogEvent);
                    } else if (answer == DialogManager.BACK) {
                        dialog.goBackToMultiple();
                        Game.core.EVENT_BUS.addEvent(dialogEvent);
                    } else setCanceled();
                }
            });
            thread.start();
        } else if (dialog.isOneComment()) {
            List<Dialog.Comment> comments = dialog.getTail();
            List<SpeakEvent> speaks = new ArrayList<>();
            for (Dialog.Comment comment : comments) {
                SpeakEvent speakEvent = new SpeakEvent(comment.getSpeaker(),"dialog."+dialogName+"."+ comment.getSrc());
                speakEvent.setCustomVoicePath(DIALOG_PATH+"/"+Game.getSettings().getString(Game.getSettings().LANG)+"/"+comment.getSpeaker()+"/"+comment.getSrc()+".wav");
                speakEvent.setTalkable(talkables.get(comment.getSpeaker()));
                speakEvent.setTextLife(-1);
                if (colors.get(comment.getSpeaker()) != null)
                    speakEvent.setColor(colors.get(comment.getSpeaker()));
                speaks.add(speakEvent);
            }
            tryindex++;
            MultiSpeakEvent multiple = new MultiSpeakEvent();
            multiple.setReadTime(PARAGRAPH_READTIME);
            multiple.addAll(speaks);
            multiple.setOnEnd(new Runnable() {
                @Override
                public void run() {
                    if(dialog.shouldEnd()){
                        setCanceled();
                        multiple.stopAndErase();
                        return;
                    }
                    if (dialog.isOnEnd()) {//at the end of tail
                        dialog.goBackToMultiple();
                    } else
                        dialog.chooseNext();
                    Game.core.EVENT_BUS.addEvent(dialogEvent);
                }
            });
            Game.core.EVENT_BUS.addEvent(multiple);
        }
    }

    private void muteTalking() {
        talkables.forEach(new BiConsumer<String, Talkable>() {
            @Override
            public void accept(String s, Talkable talkable) {
                talkable.setIsTalking(false);
            }
        });
    }

    public void setCanceled() {
        if(!canceled){
            if(onEnd!=null)
                onEnd.run();
        }
        canceled = true;
    }

    int tryindex;
    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }
}
