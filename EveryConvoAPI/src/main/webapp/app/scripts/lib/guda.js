(function(window, doc) {
    
    var log = console.log.bind(console);

    var now = function() {
        return new Date().getTime();
    }
    
    var serialize = function(form) {
        if (!form) {
            return;
        }
        var valueFieldName = form.tagName == "FORM" ? "value" : "textContent";
        var i, j, q = [];
        for (i = form.children.length - 1; i >= 0; i = i - 1) {
            var formEl = form.children[i];
            if (!formEl.name || formEl.name === "") {
                    continue;
            }
            var added = false;
            switch (formEl.nodeName) {
            case 'INPUT':
                switch (formEl.type) {
                case 'checkbox':
                case 'radio':
                    if (formEl.checked) {
                        q.push(formEl.name + "=" + encodeURIComponent(formEl[valueFieldName]));
                    }
                    added = true;
                    break;
                }
                break;
            case 'select-multiple':
                for (j = formEl.options.length - 1; j >= 0; j = j - 1) {
                    if (formEl.options[j].selected) {
                        q.push(formEl.name + "=" + encodeURIComponent(formEl.options[j][valueFieldName]));
                    }
                }
                added = true;
                break;
            }
            if( !added ) q.push(formEl.name + "=" + encodeURIComponent(formEl[valueFieldName]));
        }
        return q.join("&");
    };

    var ajax = function(type, url, params, responseType) {
        var doneFn = undefined;
        this.done = function(fn) { doneFn = fn; };

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if(xmlhttp.readyState === 4) {
                if(typeof doneFn === "function") doneFn(xmlhttp.responseText);
            }
        }
        xmlhttp.open(type, url, true);
//        xmlhttp.setRequestHeader("Content-type", "application/json");
        xmlhttp.setRequestHeader('Accept', 'application/json');
        if(type === "post") xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(params || "");

        return this;
    }
    var getAjax = function(url) { return ajax("get", url); }
    var postAjax = function(url) {
        var strings = url.split("?");
        return ajax("post", strings[0], strings[1]);
    }
    
    var dom = {
        hasClass: function(className, element) {
            if( this.extendsDom ) element = this.element;
            return ( element.className.indexOf( className ) !== -1 );
        },
        addClass: function(className, element) {
            if( this.extendsDom ) element = this.element;
            if( !element.className ) element.className = className;
            else element.className += " " + className;
            
            if( this.extendsDom ) return this;
        },
        removeClass: function(className, element) {
            if( this.extendsDom ) element = this.element;
            element.className = element.className.replace( className, "" );
            
            if( this.extendsDom ) return this;
        },
        toggleClass: function(className, element) {
            if( this.extendsDom ) element = this.element;
            if( dom.hasClass( className, element ) ) {
                dom.removeClass( className, element );
            }
            else {
                dom.addClass( className, element );
            }
            
            if( this.extendsDom ) return this;
        },
        append: function(elementToAppend, element) {
            if( this.extendsDom ) element = this.element;
            if( elementToAppend.extendsDom ) elementToAppend = elementToAppend.element;
            element.appendChild( elementToAppend );
            
            if( this.extendsDom ) return this;
        },
        insert: function(elementToInsert, element) {
            if( this.extendsDom ) element = this.element;
            if( elementToInsert.extendsDom ) elementToInsert = elementToInsert.element;
            element.insertBefore( elementToInsert, element.childNodes[0] );
            
            if( this.extendsDom ) return this;
        }
    };
    
    var Widget = function(params, tagName) {
        this.init(params, tagName);
    };
    Widget.prototype = dom;
    Widget.prototype.hide = function() {
        var el = this.element;
        this._display = el.style.display;
        el.style.display = "none";
        return this;
    };
    Widget.prototype.show = function() {
        this.element.style.display = this._display || "inline-block";
        return this;
    };
    Widget.prototype.toggle = function() {
        if( this.element.style.display == "none" ) return this.show();
        else return this.hide();
    };
    Widget.prototype.setText = function(text) {
        this.text.textContent = text;
        return this;
    };
    Widget.prototype.init = function(params, tagName) {
        if( !tagName ) {
            if( this.element ) tagName = this.element.tagName || "div";
            else tagName = "div";
        }
        
        this.extendsDom = true;
        this.element = doc.createElement( tagName );
        this.element._g = this;
        
        this.text = doc.createTextNode("");
        this.element.appendChild( this.text );
        
        for(var k in params) {
            this.element[k] = params[k];
        }
    };
    
    var View = function(params) {
        this.init( params );
        doc.body.appendChild( this.element );
    };
    View.prototype = new Widget;
    
    var Form = function(params) {
        this.init( params, "form" );
        this.element.onsubmit = this.submit;
    };
    Form.prototype = new Widget;
    Form.prototype.submit = function(e) {
        if( e ) e.preventDefault();
        var _this = this;
        postAjax( this.action + "?" + serialize(this) ).done(function(data) {
            if( _this.afterSubmit ) _this.afterSubmit( data );
        });
    };
    
    var TextArea = function(params) {
        this.init( params, "textarea" );
    };
    TextArea.prototype = new Widget;
    
    var Input = function(params) {
        this.init( params, "input" );
    };
    Input.prototype = new Widget;
    
    var Button = function(params) {
        this.init( params, "button" );
    };
    Button.prototype = new Widget;
    
    /**
     * MediWidget holds every type of media
     * @param params { mediaURL: url of media }
     */
    var MediaWidget = function(params) {
        this.create( params );        
    };
    MediaWidget.prototype = new Widget;
    MediaWidget.prototype.onload = function() {
        dom.removeClass( "loading", this );
    };
    MediaWidget.prototype.create = function(params) {
        if( !params || !params.mediaURL ) return;
        
        if( !params.className ) params.className = "";
        params.className += " loading";
        
        var mediaURL = params.mediaURL;
        switch( mediaURL.substr( mediaURL.lastIndexOf(".") + 1 ).toLowerCase() ) {
            case "jpg":
                this.init( params, "img" );
                this.element.onload = this.onload;
                this.element.src = mediaURL;
                break;
        }
    };
    
    var SidebarWidget = function(params) {
        this.init( params );
    };
    SidebarWidget.prototype = new Widget;
    
    
    window["g"] = {
        log: log,
        now: now,
        ajax: ajax,
        getAjax: getAjax,
        postAjax: postAjax,
        serialize: serialize,
        dom: dom,
        Widget: Widget,
        View: View,
        Form: Form,
        TextArea: TextArea,
        Input: Input,
        Button: Button,
        MediaWidget: MediaWidget,
        SidebarWidget: SidebarWidget
    };
    
})(window, document);

define(function() {
    return window["g"];
});