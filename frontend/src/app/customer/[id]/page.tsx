// src/app/customer/[id]/page.tsx
import Link from "next/link";
import ClientCustomerPage from "./ClientCustomerPage";
import ClientCustomerUpdatePage from "./update/ClientCustomerUpdatePage";

export default function Page({ params }: { params: { id: string } }) {
  return <ClientCustomerPage userId={params.id} />;
}
