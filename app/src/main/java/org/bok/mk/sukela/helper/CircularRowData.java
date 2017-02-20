package org.bok.mk.sukela.helper;

/**
 * Created by mk on 17.09.2016.
 */
public class CircularRowData
{
    public final String CIRCLE_COLOR;
    public final String BOX_COLOR;
    public final String CIRCLE_LETTER;
    public final String BOX_TEXT;
    public final String TAG;

    private CircularRowData(final String circle_color, final String box_color, final String circle_letter, final String box_label, final String row_tag)
    {
        this.CIRCLE_COLOR = circle_color;
        this.BOX_COLOR = box_color;
        this.CIRCLE_LETTER = circle_letter;
        this.BOX_TEXT = box_label;
        this.TAG = row_tag;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof CircularRowData))
        {
            return false;
        }
        CircularRowData r = (CircularRowData) o;
        return r.BOX_TEXT.equals(this.BOX_TEXT);
    }

    @Override
    public int hashCode()
    {
        return (CIRCLE_LETTER == null ? 0 : CIRCLE_LETTER.hashCode()) ^ (BOX_TEXT == null ? 0 : BOX_TEXT.hashCode()) ^ (CIRCLE_COLOR == null ? 0 : CIRCLE_COLOR.hashCode()) ^ (BOX_COLOR == null ? 0 : BOX_COLOR.hashCode());
    }

    public static CircularRowData create(final String circle_color, final String box_color, final String circle_letter, final String box_label, final String row_tag)
    {
        return new CircularRowData(circle_color, box_color, circle_letter, box_label, row_tag);
    }

    public static CircularRowData createSameColor(final String color, final String circle_letter, final String box_label, final String row_tag)
    {
        return new CircularRowData(color, color, circle_letter, box_label, row_tag);
    }
}
