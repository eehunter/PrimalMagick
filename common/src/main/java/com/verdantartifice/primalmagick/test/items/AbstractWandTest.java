package com.verdantartifice.primalmagick.test.items;

import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.wands.IWand;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import com.verdantartifice.primalmagick.test.TestUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractWandTest extends AbstractBaseTest {
    protected static final Map<String, Source> SOURCE_TEST_PARAMS = Sources.streamSorted().collect(Collectors.toMap(source -> source.getId().getPath(), source -> source));

    protected abstract ItemStack getTestWand();

    public Collection<TestFunction> wand_can_get_and_add_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Confirm that the wand is empty at first
                helper.assertTrue(wand.getMana(wandStack, source) == 0, "Wand is not empty as expected");

                // Add a point of real mana to the wand
                helper.assertTrue(wand.addRealMana(wandStack, source, 1) == 0, "Failed to add real mana to wand");

                // Confirm that the wand has mana in it
                helper.assertTrue(wand.getMana(wandStack, source) == 100, "Wand mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_cannot_add_too_much_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                int maxCentimana = wand.getMaxMana(wandStack);
                int attemptedRealMana = 100000;
                int expectedCentimana = (100 * attemptedRealMana) - maxCentimana;
                int actualRealMana = wand.addRealMana(wandStack, source, attemptedRealMana);

                // Confirm that the overfill for the wand is as expected
                helper.assertTrue(wand.getMana(wandStack, source) == maxCentimana, "Wand mana total is not as expected");
                helper.assertTrue(expectedCentimana == (actualRealMana * 100), "Wand overfill is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_can_get_all_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand for each source *except* the test source
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> wand.addRealMana(wandStack, s, 1));

                // Create a source list of centimana to be expected; all sources *except* the test source
                var sourceList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> sourceList.add(s, 100));

                // Confirm that the wand has the expected amount of mana in it
                helper.assertTrue(wand.getAllMana(wandStack).equals(sourceList), "Wand mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_can_consume_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand
                helper.assertTrue(wand.addRealMana(wandStack, source, 1) == 0, "Failed to add real mana to wand");

                // Confirm that a few points of centimana can be consumed
                helper.assertTrue(wand.consumeMana(wandStack, player, source, 5, helper.getLevel().registryAccess()), "Failed to consume mana from wand");

                // Confirm that the mana was deducted correctly, assuming no efficiency discounts or penalties
                helper.assertTrue(wand.getMana(wandStack, source) == 95, "Wand mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_cannot_consume_more_mana_than_it_has(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand
                helper.assertTrue(wand.addRealMana(wandStack, source, 1) == 0, "Failed to add real mana to wand");

                // Confirm that attempting to consume more mana than the wand has fails
                helper.assertFalse(wand.consumeMana(wandStack, player, source, 200, helper.getLevel().registryAccess()), "Consumption of maan succeeded when it shouldn't have");

                // Confirm that the wand still has the mana it started with
                helper.assertTrue(wand.getMana(wandStack, source) == 100, "Wand mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_can_consume_multiple_types_of_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand for each source
                Sources.getAll().forEach(s -> {
                    helper.assertTrue(wand.addRealMana(wandStack, s, 1) == 0, "Failed to add real mana to wand for " + s.getId());
                });

                // Create a source list of centimana to be deducted; all sources *except* the test source
                var sourceList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> sourceList.add(s, 5));

                // Confirm that the centimana can be consumed
                helper.assertTrue(wand.consumeMana(wandStack, player, sourceList, helper.getLevel().registryAccess()), "Failed to consume mana from wand");

                // Confirm that the mana was deducted correctly for each source, assuming no efficiency discounts or penalties
                Sources.getAll().forEach(s -> {
                    var expected = s.equals(source) ? 100 : 95;
                    helper.assertTrue(wand.getMana(wandStack, source) == expected, "Wand mana total is not as expected");
                });
            });
        });
    }

    public Collection<TestFunction> wand_cannot_consume_more_mana_than_it_has_with_multiple_types(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand for every source
                Sources.getAll().forEach(s -> wand.addRealMana(wandStack, s, 1));

                // Create a source list of centimana to be deducted; all sources *except* for the test source
                var sourceList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> sourceList.add(s, 500));

                // Confirm that attempting to deduct more mana than the wand has fails
                helper.assertFalse(wand.consumeMana(wandStack, player, sourceList, helper.getLevel().registryAccess()), "Mana consumption succeeded when it shouldn't have");

                // Confirm that the wand's mana is still in its original state
                Sources.getAll().forEach(s -> helper.assertTrue(wand.getMana(wandStack, source) == 100, "Mana total is not as expected"));
            });
        });
    }

    public Collection<TestFunction> wand_can_consume_real_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand
                helper.assertTrue(wand.addRealMana(wandStack, source, 1) == 0, "Failed to add real mana to wand");

                // Confirm that a point of real mana can be consumed
                helper.assertTrue(wand.consumeRealMana(wandStack, player, source, 1, helper.getLevel().registryAccess()), "Failed to consume mana from wand");

                // Confirm that the mana was deducted correctly, assuming no efficiency discounts or penalties, leaving the wand empty
                helper.assertTrue(wand.getAllMana(wandStack).isEmpty(), "Wand mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_cannot_consume_more_real_mana_than_it_has(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand
                helper.assertTrue(wand.addRealMana(wandStack, source, 1) == 0, "Failed to add real mana to wand");

                // Confirm that attempts to consume more real mana than the wand has fail
                helper.assertFalse(wand.consumeRealMana(wandStack, player, source, 2, helper.getLevel().registryAccess()), "Mana consumption succeeded when it shouldn't have");

                // Confirm that the wand's mana is still in its original state
                helper.assertTrue(wand.getMana(wandStack, source) == 100, "Mana total is not as expected");
            });
        });
    }

    public Collection<TestFunction> wand_can_consume_multiple_types_of_real_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand for each source
                Sources.getAll().forEach(s -> {
                    helper.assertTrue(wand.addRealMana(wandStack, s, 1) == 0, "Failed to add real mana to wand for " + s.getId());
                });

                // Create a source list of centimana to be deducted; all sources *except* the test source
                var sourceList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> sourceList.add(s, 1));

                // Confirm that the centimana can be consumed
                helper.assertTrue(wand.consumeRealMana(wandStack, player, sourceList, helper.getLevel().registryAccess()), "Failed to consume mana from wand");

                // Confirm that the mana was deducted correctly for each source, assuming no efficiency discounts or penalties
                Sources.getAll().forEach(s -> {
                    var expected = s.equals(source) ? 1 : 0;
                    helper.assertTrue(wand.getMana(wandStack, source) == expected, "Wand mana total is not as expected");
                });
            });
        });
    }

    public Collection<TestFunction> wand_cannot_consume_more_real_mana_than_it_has_with_multiple_types(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add a point of real mana to the wand for every source
                Sources.getAll().forEach(s -> wand.addRealMana(wandStack, s, 1));

                // Create a source list of real mana to be deducted; all sources *except* for the test source
                var sourceList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> sourceList.add(s, 2));

                // Confirm that attempting to deduct more mana than the wand has fails
                helper.assertFalse(wand.consumeRealMana(wandStack, player, sourceList, helper.getLevel().registryAccess()), "Mana consumption succeeded when it shouldn't have");

                // Confirm that the wand's mana is still in its original state
                Sources.getAll().forEach(s -> helper.assertTrue(wand.getMana(wandStack, source) == 100, "Mana total is not as expected"));
            });
        });
    }

    public Collection<TestFunction> wand_contains_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add some real mana to the wand for the test source
                helper.assertTrue(wand.addRealMana(wandStack, source, 2) == 0, "Failed to add real mana to wand");

                var exactCentimana = 200;
                var lessCentimana = exactCentimana - 1;
                var greaterCentimana = exactCentimana + 1;

                // Confirm that the wand recognizes it contains centimana up to the threshold of what it was given
                helper.assertTrue(wand.containsMana(wandStack, player, source, lessCentimana, helper.getLevel().registryAccess()), "Contains returned false for less than held");
                helper.assertTrue(wand.containsMana(wandStack, player, source, exactCentimana, helper.getLevel().registryAccess()), "Contains returned false for exact held");
                helper.assertFalse(wand.containsMana(wandStack, player, source, greaterCentimana, helper.getLevel().registryAccess()), "Contains returned true for greater than held");
            });
        });
    }

    public Collection<TestFunction> wand_contains_mana_list(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add some real mana to the wand for all sources except the test source
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> wand.addRealMana(wandStack, s, 1));

                // Confirm that the wand contains centimana for a list containing all source except the test source
                var greenList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> greenList.add(s, 100));
                helper.assertTrue(wand.containsMana(wandStack, player, greenList, helper.getLevel().registryAccess()), "Contains returned false for green list");

                // Confirm that the wand does not contain centimana for all sources
                var redList = SourceList.EMPTY;
                Sources.getAll().forEach(s -> redList.add(s, 100));
                helper.assertFalse(wand.containsMana(wandStack, player, redList, helper.getLevel().registryAccess()), "Contains returned true for red list");
            });
        });
    }

    public Collection<TestFunction> wand_contains_real_mana(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add some real mana to the wand for the test source
                helper.assertTrue(wand.addRealMana(wandStack, source, 2) == 0, "Failed to add real mana to wand");

                var exactRealMana = 2;
                var lessRealMana = exactRealMana - 1;
                var greaterRealMana = exactRealMana + 1;

                // Confirm that the wand recognizes it contains mana up to the threshold of what it was given
                helper.assertTrue(wand.containsRealMana(wandStack, player, source, lessRealMana, helper.getLevel().registryAccess()), "Contains returned false for less than held");
                helper.assertTrue(wand.containsRealMana(wandStack, player, source, exactRealMana, helper.getLevel().registryAccess()), "Contains returned false for exact held");
                helper.assertFalse(wand.containsRealMana(wandStack, player, source, greaterRealMana, helper.getLevel().registryAccess()), "Contains returned true for greater than held");
            });
        });
    }

    public Collection<TestFunction> wand_contains_real_mana_list(String testName, String templateName) {
        return TestUtils.createParameterizedTestFunctions(testName, templateName, SOURCE_TEST_PARAMS, (helper, source) -> {
            var player = this.makeMockServerPlayer(helper);
            var wandStack = this.getTestWand();
            helper.succeedIf(() -> {
                // Confirm that the wand was created successfully
                IWand wand = assertInstanceOf(helper, wandStack.getItem(), IWand.class, "Wand stack is not a wand as expected");

                // Add some real mana to the wand for all sources except the test source
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> wand.addRealMana(wandStack, s, 1));

                // Confirm that the wand contains real mana for a list containing all source except the test source
                var greenList = SourceList.EMPTY;
                Sources.stream().filter(s -> !s.equals(source)).forEach(s -> greenList.add(s, 1));
                helper.assertTrue(wand.containsRealMana(wandStack, player, greenList, helper.getLevel().registryAccess()), "Contains returned false for green list");

                // Confirm that the wand does not contain real mana for all sources
                var redList = SourceList.EMPTY;
                Sources.getAll().forEach(s -> redList.add(s, 1));
                helper.assertFalse(wand.containsRealMana(wandStack, player, redList, helper.getLevel().registryAccess()), "Contains returned true for red list");
            });
        });
    }
}
