package com.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;

public class TsxAnimationLoader {

    private TextureRegion[] allSpriteFrames; // Mảng chứa tất cả các TextureRegion từ TextureAtlas

    /**
     * Khởi tạo loader và nạp trước các TextureRegion từ TextureAtlas.
     * @param atlas TextureAtlas đã được nạp.
     * @param totalFramesInAtlas Tổng số frame trong atlas (ví dụ: 48 cho Knight_A).
     */
    public TsxAnimationLoader(TextureAtlas atlas, int totalFramesInAtlas) {
        if (atlas == null) {
            throw new IllegalArgumentException("TextureAtlas không được null.");
        }
        this.allSpriteFrames = new TextureRegion[totalFramesInAtlas];
        for (int i = 0; i < totalFramesInAtlas; i++) {
            // Giả sử các region trong atlas được đặt tên là "0", "1", "2",...
            this.allSpriteFrames[i] = atlas.findRegion(String.valueOf(i));
            if (this.allSpriteFrames[i] == null) {
                Gdx.app.error("TsxAnimationLoader", "Không tìm thấy TextureRegion '" + i + "' trong atlas khi nạp trước.");
                // Bạn có thể muốn xử lý lỗi này nghiêm ngặt hơn
            }
        }
    }

    /**
     * Khởi tạo loader với một mảng TextureRegion đã được nạp sẵn.
     * @param preloadedFrames Mảng TextureRegion đã được nạp.
     */
    public TsxAnimationLoader(TextureRegion[] preloadedFrames) {
        if (preloadedFrames == null) {
            throw new IllegalArgumentException("Mảng preloadedFrames không được null.");
        }
        this.allSpriteFrames = preloadedFrames;
    }

    /**
     * Đọc tệp .tsx và tạo ra một ObjectMap chứa các Animation.
     * Key của map là tên animation (ví dụ: "tile_0_animation" hoặc tên từ thuộc tính "type" của tile).
     * @param tsxFilePath Đường dẫn đến tệp .tsx trong assets.
     * @return ObjectMap chứa các animation đã nạp, hoặc null nếu có lỗi.
     */
    public ObjectMap<String, Animation<TextureRegion>> loadAnimationsFromTsx(String tsxFilePath) {
        ObjectMap<String, Animation<TextureRegion>> animationsMap = new ObjectMap<>();
        FileHandle tsxFile = Gdx.files.internal(tsxFilePath); // Sử dụng Gdx.files để truy cập tệp
        XmlReader xmlReader = new XmlReader();

        if (!tsxFile.exists()) {
            Gdx.app.error("TsxAnimationLoader", "Không tìm thấy tệp TSX: " + tsxFilePath);
            return null;
        }

        XmlReader.Element rootElement = xmlReader.parse(tsxFile); // Phần tử gốc là <tileset>

        Array<XmlReader.Element> tileElements = rootElement.getChildrenByName("tile");

        for (XmlReader.Element tileElement : tileElements) {
            String baseTileId = tileElement.getAttribute("id"); // ID của <tile> gốc định nghĩa animation
            XmlReader.Element animationElement = tileElement.getChildByName("animation");

            if (animationElement != null) {
                Array<XmlReader.Element> frameElements = animationElement.getChildrenByName("frame");
                if (frameElements.size == 0) {
                    Gdx.app.log("TsxAnimationLoader", "Tile ID " + baseTileId + " có khối <animation> rỗng.");
                    continue;
                }

                Array<TextureRegion> keyFramesForAnimation = new Array<>();
                float frameDuration = 0.1f; // Giá trị mặc định, sẽ được ghi đè

                // Trong tệp .tsx của bạn, tất cả <frame> trong một <animation> có cùng duration.
                // Lấy duration từ frame đầu tiên.
                if (frameElements.first().hasAttribute("duration")) {
                    frameDuration = frameElements.first().getIntAttribute("duration") / 1000.0f; // Chuyển mili giây sang giây
                } else {
                    Gdx.app.log(
                        "TsxAnimationLoader", "Frame đầu tiên của animation cho tile ID " + baseTileId +
                        " không có thuộc tính 'duration'. Sử dụng mặc định: " + frameDuration + "s");
                }

                for (XmlReader.Element frameElement : frameElements) {
                    int frameAtlasNumericId = frameElement.getIntAttribute("tileid"); // Đây là ID của frame trong atlas

                    if (frameAtlasNumericId >= 0 && frameAtlasNumericId < allSpriteFrames.length && allSpriteFrames[frameAtlasNumericId] != null) {
                        keyFramesForAnimation.add(allSpriteFrames[frameAtlasNumericId]);
                    } else {
                        Gdx.app.error("TsxAnimationLoader", "ID frame không hợp lệ (" + frameAtlasNumericId +
                            ") cho animation của tile ID " + baseTileId + ". Bỏ qua frame này.");
                    }
                }

                if (keyFramesForAnimation.size > 0) {
                    // Đặt tên cho animation. Bạn có thể dùng ID của tile,
                    // hoặc tốt hơn là dùng thuộc tính "type" của tile trong Tiled Editor để đặt tên có ý nghĩa.
                    String animationName;
                    if (tileElement.hasAttribute("type") && !tileElement.getAttribute("type").isEmpty()) {
                        animationName = tileElement.getAttribute("type"); // Ví dụ: "knight_idle_down"
                    } else {
                        animationName = "tile_" + baseTileId + "_animation"; // Tên mặc định
                    }

                    // Giả sử tất cả animation lặp lại. Bạn có thể thêm thuộc tính tùy chỉnh vào .tsx để kiểm soát PlayMode.
                    Animation<TextureRegion> animation = new Animation<>(frameDuration, keyFramesForAnimation, Animation.PlayMode.LOOP);
                    animationsMap.put(animationName, animation);
                    Gdx.app.log("TsxAnimationLoader", "Đã nạp animation: '" + animationName + "' với " + keyFramesForAnimation.size + " frames.");
                }
            }
        }

        return animationsMap;
    }
}
