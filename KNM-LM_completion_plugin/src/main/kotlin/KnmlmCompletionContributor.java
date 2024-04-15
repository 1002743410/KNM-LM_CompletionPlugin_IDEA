import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 在用户编写代码时，根据当前代码上下文向用户提供补全建议，并在用户触发补全时执行 HTTP 请求来获取预测结果.
 */
public class KnmlmCompletionContributor extends CompletionContributor {
    /**
     * 从外部服务获取代码补全建议.
     * 首先获取当前位置的 PsiElement 对象，然后从其父级元素中找到包含当前方法的 PsiMethod.
     * 接着，获取该方法的文本内容，并将 PRED 替换为 ??IntellijIdeaRulezzz.
     * 然后，执行 HTTP POST 请求，将替换后的代码发送给外部服务，并接收返回的预测结果.
     * 最后，将预测结果转换为 JSON 格式，提取出预测建议和对应的分数，并构造成 KnmlmLookupElement 对象的列表返回.
     * @param parameters
     * @param maxCompletions
     * @return
     * @throws Exception
     */
    public ArrayList<LookupElement> retrieveCompletions(CompletionParameters parameters,int maxCompletions) throws Exception{
        ArrayList<LookupElement> completions = new ArrayList<>();long start, end;
        start = System.currentTimeMillis();
        PsiElement element = parameters.getPosition();
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        String source = containingMethod.getText();
        source = source.replace("??IntellijIdeaRulezzz", "PRED");
        System.out.println(source);
        end = System.currentTimeMillis();
        System.out.println("get Method: " + (end-start) + "ms");
        start = System.currentTimeMillis();
        JSONObject predictionObject = HttpUtils.doPost(source);
        end = System.currentTimeMillis();
        System.out.println("do Post: "+ (end-start) + "ms");
        JSONArray predictions = (JSONArray) predictionObject.get("predictions");
        JSONArray predictionScores = (JSONArray) predictionObject.get("prediction_scores");
//        String[] predictions = new String[] {
//                "int a = 1",
//                "a = a + b",
//                "a = a + a",
//                "int b = 1",
//                "b = b - 1"
//        };
//        String[] predictionScores = new String[]{
//                "44.96%", "25.78%", "19.99%", "17.22%", "11.56%"
//        };
        for (int i = 0; i < Math.max(5, maxCompletions); ++i) {
            KnmlmLookupElement completion = new KnmlmLookupElement(
                    i,
                    (String)predictions.get(i),
                    (String)predictionScores.get(i)
//                    predictions[i],
//                    predictionScores[i]
            );
            completions.add(completion);
        }
        return completions;
    }

    /**
     * 填充代码补全的变体.
     * 首先检查是否触发了代码补全，然后调用 retrieveCompletions 方法获取补全建议.
     * 最后，将补全建议添加到结果集中，并使用自定义的 CompletionSorter 进行排序.
     * @param parameters
     * @param result
     */
    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result){
        System.out.println(parameters.getOffset());
        boolean trigger = endsWith(
                parameters.getEditor().getDocument(),
                parameters.getOffset() - result.getPrefixMatcher().getPrefix().length(),
                "??");

        if (!trigger) {
            return;
        }

        int baseMaxCompletions = 5;
        ArrayList<LookupElement> completions = null;
        try {
            completions = this.retrieveCompletions(parameters, baseMaxCompletions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (completions != null) {
            long start, end;
            start = System.currentTimeMillis();
            // sort completions by their index(score)
            result = result.withRelevanceSorter(CompletionSorter.emptySorter().weigh(new KnmlmWeighter()));
            result.addAllElements(completions);
            end = System.currentTimeMillis();
            System.out.println("show: " + (end-start) + "ms");
        }
    }


    /**
     * 检查给定的 Document 对象中从指定位置开始的文本是否以给定的字符串结尾.
     * @param doc
     * @param pos
     * @param s
     * @return
     */
    static  boolean endsWith(Document doc,int pos,String s){
        int begin = pos - s.length();
        if (begin < 0 || pos > doc.getTextLength()) {
            return false;
        } else {
            String tail = doc.getText(new TextRange(begin, pos));
            return tail.equals(s);
        }
    }

    /**
     * 指示是否自动弹出补全窗口.
     * 在这里，总是返回 true，表示始终自动弹出.
     * @param position
     * @param typeChar
     * @return
     */
    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position,char typeChar){
        return true;
    }

}
