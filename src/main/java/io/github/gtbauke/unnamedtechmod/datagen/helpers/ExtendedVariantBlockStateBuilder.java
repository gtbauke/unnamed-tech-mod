package io.github.gtbauke.unnamedtechmod.datagen.helpers;

import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;

import java.util.function.Function;

public class ExtendedVariantBlockStateBuilder {
    public VariantBlockStateBuilder builder;

    public ExtendedVariantBlockStateBuilder(VariantBlockStateBuilder builder) {
        this.builder = builder;
    }

    public static ExtendedVariantBlockStateBuilder getExtendedVariantBuilder(VariantBlockStateBuilder builder)
    {
        return new ExtendedVariantBlockStateBuilder(builder);
    }

    public ExtendedVariantBlockStateBuilder addModelForHorizontalDirectionsWith(ModelFile model, Function<VariantBlockStateBuilder.PartialBlockstate, VariantBlockStateBuilder.PartialBlockstate> doWiths) {
        for (int i = 0; i <= 3; i++) {
            Direction dir = Direction.from2DDataValue((i + 2) % 4);
            VariantBlockStateBuilder.PartialBlockstate partial = builder
                    .partialState()
                    .with(AbstractMachineBlock.FACING, dir);

            partial = doWiths.apply(partial);

            builder = partial.modelForState()
                    .rotationY(i * 90)
                    .modelFile(model)
                    .addModel();
        }

        return this;
    }
}
