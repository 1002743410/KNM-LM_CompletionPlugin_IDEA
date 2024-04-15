import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.jetbrains.annotations.NotNull;

/**
 * 表示代码补全的建议项，表示代码补全的建议项，并定义了建议项被插入时的行为以及呈现方式.
 */
public class KnmlmLookupElement extends LookupElement {
    /**
     * 建议项的代码
     */
    public final String code;
    /**
     * 建议项的分数
     */
    public final String score;
    /**
     * 建议项的索引
     */
    public final int index;

    public KnmlmLookupElement(int index,String code,String score){
        this.index=index;
        this.code=code;
        this.score=score;
    }

    /**
     * 返回建议项的代码作为查找字符串.
     * @return
     */
    @NotNull
    @Override
    public String getLookupString(){
        return this.code;
    }

    /**
     * 处理建议项被插入时的行为.
     * @param context an object containing useful information about the circumstances of insertion
     */
    @Override
    public void handleInsert(InsertionContext context){
        int begin=context.getStartOffset();
        context.getDocument().deleteString(begin-2,begin);
    }

    /**
     * 呈现建议项
     * @param presentation
     */
    @Override
    public void renderElement(LookupElementPresentation presentation){
        presentation.setItemText(this.code);
        presentation.setItemTextBold(true);
        presentation.setTypeText(this.score);
    }
}
