package mc.cooble.dialog;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej on 21.7.2017.
 */
public class Dialog {

    public boolean hasComments() {
        return currentComments.size()!=0;
    }
    public boolean isOneComment(){
        return currentComments.size()==1;
    }

    public static class Comment{
        List<Comment> children;
        Comment parent;
        String speaker;
        String src;
        /**
         * tells when comment should be displayed if current bool nbt is set in Game.getWorld().getModule().nbt
         * if null it will be displayed all the time
         */
        String nbtEnable;
        /**
         * if same as dialogname it is currently in -> nothing happens
         * if null -> end comment (when reached -> dialog is over)
         * if some other dialogName -> call new DialogEvent()
         */
        String newDialogName;


        public Comment(Comment parent){
            this.parent = parent;
            children=new ArrayList<>();
        }
        public Comment(Comment parent,String src){
            this(parent);
            setSrc(src);
        }

        @Nullable
        public String getNBTEnable() {
            return nbtEnable;
        }

        public void setNbtEnable(@Nullable String nbtEnable) {
            this.nbtEnable = nbtEnable;
        }

        public void setNewDialogName(@Nullable String newDialogName) {
            this.newDialogName = newDialogName;
        }

        @Nullable
        public String getNewDialogName() {
            return newDialogName;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        void addChild(Comment child){
            children.add(child);
        }

        List<Comment> getChildren() {
            return children;
        }

        public String getSrc() {
            return src;
        }

        public String getSpeaker() {
            return speaker;
        }

        public void setSpeaker(String speaker) {
            this.speaker = speaker;
        }

        Comment getParent() {
            return parent;
        }
        List<Comment> getSiblings(){
            return parent.getChildren();
        }
    }
    private List<Comment> currentComments;

    private List<Comment> lastGroup;
    private List<Comment> XLastGroup;
    private Comment root;

    public Dialog(Comment root){
        this.root = root;
    }
    public List<Comment> start() {
        return choose(root);
    }
    public List<Comment> choose(Comment comment){
        currentComments=comment.getChildren();
        if(comment.getChildren().size()>1) {
            if(!currentComments.equals(lastGroup)) {//new group
                XLastGroup=lastGroup;
                lastGroup = currentComments;
            }
        }
        return getCurrentComments();
    }
    public List<Comment> choose(int index){
      return choose(currentComments.get(index));
    }
    public List<Comment> chooseNext(){
       choose(currentComments.get(0));
        return getCurrentComments();
    }
    public List<Comment> peak(Comment comment){
        return comment.getChildren();
    }
    public List<Comment> peak(int index){
      return peak(currentComments.get(index));
    }
    public List<Comment> peakNext(){
       return peak(currentComments.get(0));
    }
    public List<Comment> goBack(){
        try {
            currentComments=currentComments.get(0).getParent().getParent().getChildren();

        }catch (NullPointerException e){
            start();
        }
        return getCurrentComments();
    }
    public List<Comment> goBackToMultiple(){
        if(isMultipleComments()&&XLastGroup!=null) {
            currentComments = XLastGroup;
            lastGroup=currentComments;
        }
        else if(lastGroup!=null)
            currentComments=lastGroup;
        return getCurrentComments();
    }

    public List<Comment> getCurrentComments() {
        return currentComments;
    }

    /**
     * goes deeper till end of current tail
     * runs only if isOneComment()
     * @return list of comments which have one and only one child
     * stops att multiple
     */
    public List<Comment> getTail(){
        if(!isOneComment())
            return new ArrayList<>(0);
        ArrayList<Comment> out  =new ArrayList<>();
        Comment current = getCurrentComments().get(0);
        while (current.getChildren().size()==1){
            out.add(current);
            current=current.getChildren().get(0);
        }
        out.add(current);
       // currentComments.clear();//never do this!!
        currentComments=current.getSiblings();
        return out;
    }


    public boolean isMultipleComments(){
        return currentComments.size()>1;
    }

    public boolean isOnEnd(){
        return currentComments.size()==1&&currentComments.get(0).getChildren().size()==0;
    }

    /**
     *
     * @return true if current comment is end comment to set dialog over/end/the end
     */
    public boolean shouldEnd(){
        return isOnEnd()&& currentComments.get(0).getNewDialogName()==null;
    }
}
