

import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.splitting.ISplitCharacters;

public final class CustomSplitCharacters implements ISplitCharacters {

    @Override
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        if (!text.get(glyphPos).hasValidUnicode()) {
            return false;
        }
        int charCode = text.get(glyphPos).getUnicode();
        //Check if a hyphen proceeds a digit to denote negative value
        if ((glyphPos == 0) && (charCode == '-') && (text.size() - 1 > glyphPos) && (isADigitChar(text, glyphPos + 1))) {
            return false;
        }
        return true;
    }

    private boolean isADigitChar(GlyphLine text, int glyphPos) {
       return Character.isDigit(text.get(glyphPos).getChars()[0]);
    }

}
