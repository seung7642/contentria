import React from 'react';

export default function MdxLayout({ children }: { children: React.ReactNode }) {
  return (
    <article className="prose max-w-none border px-8 py-8 prose-headings:mt-8 prose-headings:font-semibold">
      {children}
    </article>
  );
}
