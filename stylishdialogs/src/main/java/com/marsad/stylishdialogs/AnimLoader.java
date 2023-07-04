package com.marsad.stylishdialogs;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import androidx.annotation.AnimRes;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

/**
 * Enforce Valid Animation Resource IDs with @AnimRes Annotation.
 * <p>
 * To ensure the usage of valid animation resource IDs at compile-time and catch potential errors early,
 * the @AnimRes annotation is recommended. This annotation is part of the Android Support Annotations library
 * and can be used to enforce the correct usage of animation resource IDs within the AnimLoader class.
 */
public class AnimLoader {

    private static final SparseArray<Animation> animationCache = new SparseArray<>();

    /**
     * Loads an animation from the specified animation resource ID.
     *
     * @param context The context used to access resources.
     * @param id      The animation resource ID to load.
     * @return The loaded Animation object.
     * @throws Resources.NotFoundException If the animation resource ID is not found.
     */
    public static Animation loadAnimation(Context context, @AnimRes int id)
            throws Resources.NotFoundException {

        Animation animation = animationCache.get(id);

        if (animation != null) {
            return animation;
        }

        try (XmlResourceParser parser = context.getResources().getAnimation(id)) {
            animation = createAnimationFromXml(context, parser);
            animationCache.put(id, animation);
            return animation;
        } catch (XmlPullParserException | IOException ex) {

            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex.getCause());
            throw rnf;

        }

    }

    /**
     * Creates an animation object from the XML resource.
     *
     * @param context The context to access resources.
     * @param parser  The XML parser for the animation resource.
     * @return The created animation object.
     * @throws XmlPullParserException If an error occurs during XML parsing.
     * @throws IOException            If an I/O error occurs.
     */
    private static Animation createAnimationFromXml(Context context, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(context, parser, null, Xml.asAttributeSet(parser));
    }

    /**
     * Creates an animation object from the XML resource.
     *
     * @param context The context to access resources.
     * @param parser  The XML parser for the animation resource.
     * @param parent  The parent animation set.
     * @param attrs   The attribute set for the animation.
     * @return The created animation object.
     * @throws XmlPullParserException If an error occurs during XML parsing.
     * @throws IOException            If an I/O error occurs.
     */
    private static Animation createAnimationFromXml(Context context, XmlPullParser parser,
                                                    AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {

        Animation anim = null;

        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            switch (name) {
                case "set":
                    anim = new AnimationSet(context, attrs);
                    createAnimationFromXml(context, parser, (AnimationSet) anim, attrs);
                    break;
                case "alpha":
                    anim = new AlphaAnimation(context, attrs);
                    break;
                case "scale":
                    anim = new ScaleAnimation(context, attrs);
                    break;
                case "rotate":
                    anim = new RotateAnimation(context, attrs);
                    break;
                case "translate":
                    anim = new TranslateAnimation(context, attrs);
                    break;
                default:
                    try {
                        anim = (Animation) Class.forName(name).getConstructor(Context.class, AttributeSet.class).newInstance(context, attrs);
                    } catch (Exception te) {
                        throw new RuntimeException("Unknown animation name: " + parser.getName() + " error:" + te.getMessage());
                    }
                    break;
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }

        return anim;

    }

}
