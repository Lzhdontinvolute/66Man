
import com.lzh.financial.code.service.UserService;
import com.lzh.financial.code.test.generator.domain.JsonTest;
import com.lzh.financial.code.test.generator.mapper.JsonTestMapper;
import com.lzh.financial.code.test.generator.service.JsonTestService;
import org.junit.jupiter.api.Test;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class test {
    @Autowired
    private JsonTestService jsonTestService;
    @Autowired
    private UserService userService;

    @Test
    public  void amain() {
        JsonTest jsonTest = jsonTestService.select();
        int i = userService.hashCode();
        System.out.println(jsonTest);
    }

    @Test
    public void test() throws IOException {
        //功能：从word中找出对应的加密后的数据，加密算法是hash.md5_crypt
        //原始数据
        List<String> word = new ArrayList<>();
        word.add("123");
        word.add("456");
        //加密后数据
        List<String> cryptWord = new ArrayList<>();
        cryptWord.add("$1$KP074k5L$GkgfZVwByM0FQt4l.KLoh/");
        cryptWord.add("$1$zTxoz1fL$HKSbEyNFHGkLgAHZUTjmz.");

        String pythonFilePath = "test.py";
        String pythonFileMethod = "verify";

        PythonInterpreter interpreter = new PythonInterpreter();
        ClassPathResource resource = new ClassPathResource(pythonFilePath);
        InputStream inputStream = resource.getInputStream();
        interpreter.execfile(inputStream);
        PyFunction verify = interpreter.get(pythonFileMethod, PyFunction.class);
        //调用
        PyObject pyObject = verify.__call__(new PyList(word), new PyList(cryptWord));
        List<String> result = (List<String>)pyObject.__tojava__(List.class);
        System.out.println(result);

        interpreter.close();

    }


}
