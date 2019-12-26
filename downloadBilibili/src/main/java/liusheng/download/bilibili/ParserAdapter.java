package liusheng.download.bilibili;

import liusheng.downloadInterface.Parser;

import java.io.IOException;

public interface ParserAdapter {

    Parser<String,?> handle(AdapterParam adapterParam) throws IOException;
}
