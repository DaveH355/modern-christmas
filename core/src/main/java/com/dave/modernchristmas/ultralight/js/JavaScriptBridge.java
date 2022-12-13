package com.dave.modernchristmas.ultralight.js;

import com.dave.modernchristmas.ultralight.util.ViewContextProvider;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.databind.Databind;
import com.labymedia.ultralight.databind.DatabindConfiguration;
import com.labymedia.ultralight.javascript.JavascriptContext;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.javascript.JavascriptGlobalContext;
import com.labymedia.ultralight.javascript.JavascriptObject;


public class JavaScriptBridge {
    private Databind databind;
    private UltralightView view;

    public JavaScriptBridge(UltralightView view) {
        this.view = view;
        databind = new Databind(DatabindConfiguration
                .builder()
                .contextProviderFactory(new ViewContextProvider.Factory(view))
                .build()
        );

    }

    /**
     * Exposes an object fully to Javascript for access
     * @param object The object to expose
     * @param name The name Javascript will use to reference this object
     * @param context The Javascript context of the view
     */
    public void setContext(Object object, String name, JavascriptContext context) {
        JavascriptGlobalContext globalContext = context.getGlobalContext();
        JavascriptObject globalObject = globalContext.getGlobalObject();

        globalObject.setProperty(name, databind.getConversionUtils().toJavascript(context, object), 0 );
    }

    public JavascriptContextLock getContextLock() {
        return view.lockJavascriptContext();
    }
}
