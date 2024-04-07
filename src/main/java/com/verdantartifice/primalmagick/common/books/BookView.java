package com.verdantartifice.primalmagick.common.books;

import java.util.Optional;

import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

/**
 * Record containing all the parameters needed to render a player's view of an encoded static book.
 * 
 * @author Daedalus4096
 */
public record BookView(ResourceKey<?> bookKey, ResourceKey<?> languageId, int comprehension) {
    public BookView withComprehension(int newComprehension) {
        return new BookView(this.bookKey, this.languageId, newComprehension);
    }
    
    /**
     * Optionally gets a reference holder for this view's book definition, should that definition be found.
     * 
     * @param registryAccess a registry accessor
     * @return an optional reference holder for this view's book definition
     */
    public Optional<Holder.Reference<BookDefinition>> getBookDefinition(RegistryAccess registryAccess) {
        return this.bookKey().cast(RegistryKeysPM.BOOKS).flatMap(key -> registryAccess.registryOrThrow(RegistryKeysPM.BOOKS).getHolder(key));
    }
    
    /**
     * Gets a reference holder for this view's book definitions, or the given default if the view's definition cannot be found.
     * Throws if the given default also cannot be found.
     * 
     * @param registryAccess a registry accessor
     * @param defaultBook the default book definition to use if the view's is not found
     * @return a reference holder for this view's book definition, or the given default
     */
    public Holder.Reference<BookDefinition> getBookDefinitionOrDefault(RegistryAccess registryAccess, ResourceKey<BookDefinition> defaultBook) {
        return registryAccess.registryOrThrow(RegistryKeysPM.BOOKS)
                .getHolder(this.bookKey().cast(RegistryKeysPM.BOOKS).orElse(defaultBook))
                .orElse(registryAccess.registryOrThrow(RegistryKeysPM.BOOKS).getHolderOrThrow(defaultBook));
    }
    
    /**
     * Gets a reference holder for this view's book definition, or throws if the view's book definition cannot be found.
     * 
     * @param registryAccess a registry accessor
     * @return a reference holder for this view's book definition
     */
    public Holder.Reference<BookDefinition> getBookDefinitionOrThrow(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(RegistryKeysPM.BOOKS).getHolderOrThrow(this.bookKey().cast(RegistryKeysPM.BOOKS).orElseThrow());
    }
    
    /**
     * Optionally gets a reference holder for this view's book language, should that language be found.
     * 
     * @param registryAccess a registry accessor
     * @return an optional reference holder for this view's book language
     */
    public Optional<Holder.Reference<BookLanguage>> getLanguage(RegistryAccess registryAccess) {
        return this.languageId().cast(RegistryKeysPM.BOOK_LANGUAGES).flatMap(key -> registryAccess.registryOrThrow(RegistryKeysPM.BOOK_LANGUAGES).getHolder(key));
    }
    
    /**
     * Gets a reference holder for this view's book language, or the given default if the view's language cannot be found.
     * Throws if the given default also cannot be found.
     * 
     * @param registryAccess a registry accessor
     * @param defaultLang the default language to use if the view's is not found
     * @return a reference holder for this view's book language, or the given default
     */
    public Holder.Reference<BookLanguage> getLanguageOrDefault(RegistryAccess registryAccess, ResourceKey<BookLanguage> defaultLang) {
        return registryAccess.registryOrThrow(RegistryKeysPM.BOOK_LANGUAGES)
                .getHolder(this.languageId().cast(RegistryKeysPM.BOOK_LANGUAGES).orElse(defaultLang))
                .orElse(registryAccess.registryOrThrow(RegistryKeysPM.BOOK_LANGUAGES).getHolderOrThrow(defaultLang));
    }
    
    /**
     * Gets a reference holder for this view's book language, or throws if the view's language cannot be found.
     * 
     * @param registryAccess a registry accessor
     * @return a reference holder for this view's book language
     */
    public Holder.Reference<BookLanguage> getLanguageOrThrow(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(RegistryKeysPM.BOOK_LANGUAGES).getHolderOrThrow(this.languageId().cast(RegistryKeysPM.BOOK_LANGUAGES).orElseThrow());
    }
}
