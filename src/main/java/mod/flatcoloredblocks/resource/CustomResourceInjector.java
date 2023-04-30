package mod.flatcoloredblocks.resource;

import mod.flatcoloredblocks.client.ClientSide;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.function.Predicate;

public class CustomResourceInjector implements ISelectiveResourceReloadListener {
    public static CustomFileProvider generatedFiles = new CustomFileProvider();

    public static void addResource(
            String folder,
            String resourceName,
            String ext,
            byte[] data) {
        generatedFiles.fakeFiles.put(folder + "/" + resourceName + ext, data);
    }

    public static void addResource(String resourceName, String ext, byte[] data){
        generatedFiles.fakeFiles.put(resourceName+ext,data);
    }

    @Override
    public void onResourceManagerReload(
            IResourceManager resourceManager,
            Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(VanillaResourceType.MODELS)) {
            if (resourceManager instanceof SimpleReloadableResourceManager) {

               ((SimpleReloadableResourceManager) resourceManager).addResourcePack(generatedFiles);
             ClientSide.instance.createResources();
            }
        }
    }

}
